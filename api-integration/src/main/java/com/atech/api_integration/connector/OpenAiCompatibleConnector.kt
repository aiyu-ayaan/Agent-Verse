package com.atech.api_integration.connector

import com.atech.api_integration.network.OpenAiChatCompletionRequest
import com.atech.api_integration.network.OpenAiChatCompletionChunk
import com.atech.api_integration.network.OpenAiChatCompletionResponse
import com.atech.api_integration.network.OpenAiMessage
import com.atech.api_integration.network.OpenAiModelsResponse
import com.atech.api_integration.network.OpenAiUsage
import com.atech.api_integration_common.contract.AvProviderConnector
import com.atech.api_integration_common.error.AvApiError
import com.atech.api_integration_common.model.AvChatRequest
import com.atech.api_integration_common.model.AvChatResponse
import com.atech.api_integration_common.model.AvMessage
import com.atech.api_integration_common.model.AvModelSummary
import com.atech.api_integration_common.model.AvProvider
import com.atech.api_integration_common.model.AvProviderCredentials
import com.atech.api_integration_common.model.AvRole
import com.atech.api_integration_common.model.AvTokenUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

abstract class OpenAiCompatibleConnector(
    private val okHttpClient: OkHttpClient,
    private val json: Json,
) : AvProviderConnector {

    protected abstract val defaultBaseUrl: String

    protected abstract fun buildHeaders(credentials: AvProviderCredentials): Map<String, String>

    override suspend fun chatCompletion(
        request: AvChatRequest,
        credentials: AvProviderCredentials,
    ): Result<AvChatResponse> = withContext(Dispatchers.IO) {
        runCatching {
            require(request.provider == provider) {
                "Request provider ${request.provider} does not match connector $provider"
            }

            val payload = OpenAiChatCompletionRequest(
                model = request.modelId,
                messages = request.messages.map { it.toNetwork() },
                temperature = request.modelConfig.temperature,
                maxTokens = request.modelConfig.maxTokens,
                topP = request.modelConfig.topP,
                frequencyPenalty = request.modelConfig.frequencyPenalty,
                presencePenalty = request.modelConfig.presencePenalty,
                stop = request.modelConfig.stopSequences.takeIf { it.isNotEmpty() },
                stream = request.modelConfig.stream,
            )

            val body = json.encodeToString(payload)
            val raw = executePost(
                url = buildUrl(credentials, "chat/completions"),
                headers = buildHeaders(credentials),
                body = body,
            )

            if (request.modelConfig.stream) {
                parseStreamCompletion(raw, request)
            } else {
                parseStandardCompletion(raw, request)
            }
        }.recoverCatching { throwable ->
            throw throwable.toApiError(provider)
        }
    }

    override suspend fun listModels(credentials: AvProviderCredentials): Result<List<AvModelSummary>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val raw = executeGet(
                    url = buildUrl(credentials, "models"),
                    headers = buildHeaders(credentials),
                )
                val parsed = json.decodeFromString<OpenAiModelsResponse>(raw)
                parsed.data.map {
                    AvModelSummary(
                        id = it.id,
                        provider = provider,
                        ownedBy = it.ownedBy,
                        contextWindow = it.contextLength,
                    )
                }
            }.recoverCatching { throwable ->
                throw throwable.toApiError(provider)
            }
        }

    private fun executePost(url: String, headers: Map<String, String>, body: String): String {
        val requestBody = body.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .applyHeaders(headers)
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            val payload = response.body.string()
            if (!response.isSuccessful) {
                throw HttpStatusException(response.code, payload)
            }
            return payload
        }
    }

    private fun executeGet(url: String, headers: Map<String, String>): String {
        val request = Request.Builder()
            .url(url)
            .get()
            .applyHeaders(headers)
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            val payload = response.body.string()
            if (!response.isSuccessful) {
                throw HttpStatusException(response.code, payload)
            }
            return payload
        }
    }

    private fun Request.Builder.applyHeaders(headers: Map<String, String>): Request.Builder {
        headers.forEach { (name, value) ->
            if (value.isNotBlank()) {
                header(name, value)
            }
        }
        return this
    }

    private fun buildUrl(credentials: AvProviderCredentials, path: String): String {
        val base = credentials.baseUrl?.takeIf { it.isNotBlank() } ?: defaultBaseUrl
        val normalizedBase = if (base.endsWith('/')) base else "$base/"
        return "$normalizedBase$path"
    }

    private fun AvMessage.toNetwork(): OpenAiMessage {
        val mappedRole = when (role) {
            AvRole.SYSTEM -> "system"
            AvRole.USER -> "user"
            AvRole.ASSISTANT -> "assistant"
            AvRole.TOOL -> "tool"
        }
        return OpenAiMessage(role = mappedRole, content = content, name = name)
    }

    private fun OpenAiMessage.toDomain(): AvMessage {
        val mappedRole = when (role.lowercase()) {
            "system" -> AvRole.SYSTEM
            "assistant" -> AvRole.ASSISTANT
            "tool" -> AvRole.TOOL
            else -> AvRole.USER
        }
        return AvMessage(role = mappedRole, content = content, name = name)
    }

    private fun parseStandardCompletion(raw: String, request: AvChatRequest): AvChatResponse {
        val parsed = json.decodeFromString<OpenAiChatCompletionResponse>(raw)
        val choice = parsed.choices.firstOrNull() ?: error("No completion returned")
        val assistantMessage = choice.message?.toDomain()
            ?: AvMessage(role = AvRole.ASSISTANT, content = "")

        return AvChatResponse(
            requestId = parsed.id ?: "",
            provider = provider,
            modelId = parsed.model ?: request.modelId,
            createdAtEpochMs = (parsed.created ?: System.currentTimeMillis() / 1000L) * 1000L,
            message = assistantMessage,
            finishReason = choice.finishReason,
            usage = parsed.usage.toDomain(),
            rawResponse = raw,
        )
    }

    private fun parseStreamCompletion(raw: String, request: AvChatRequest): AvChatResponse {
        if (!raw.contains("data:")) {
            return parseStandardCompletion(raw, request)
        }

        var requestId = ""
        var model = request.modelId
        var createdAtEpochMs = System.currentTimeMillis()
        var role: AvRole = AvRole.ASSISTANT
        var finishReason: String? = null
        var usage: OpenAiUsage? = null
        val content = StringBuilder()

        raw.lineSequence()
            .map { it.trim() }
            .filter { it.startsWith("data:") }
            .forEach { line ->
                val payload = line.removePrefix("data:").trim()
                if (payload == "[DONE]" || payload.isEmpty()) {
                    return@forEach
                }

                val chunk = runCatching {
                    json.decodeFromString<OpenAiChatCompletionChunk>(payload)
                }.getOrNull() ?: return@forEach

                if (!chunk.id.isNullOrBlank()) requestId = chunk.id
                if (!chunk.model.isNullOrBlank()) model = chunk.model
                if (chunk.created != null) createdAtEpochMs = chunk.created * 1000L
                if (chunk.usage != null) usage = chunk.usage

                val choice = chunk.choices.firstOrNull()
                choice?.delta?.role?.let { role = mapRole(it) }
                choice?.delta?.content?.let(content::append)
                choice?.finishReason?.takeIf { it.isNotBlank() }?.let { finishReason = it }
            }

        return AvChatResponse(
            requestId = requestId,
            provider = provider,
            modelId = model,
            createdAtEpochMs = createdAtEpochMs,
            message = AvMessage(role = role, content = content.toString()),
            finishReason = finishReason,
            usage = usage.toDomain(),
            rawResponse = raw,
        )
    }

    private fun mapRole(value: String): AvRole = when (value.lowercase()) {
        "system" -> AvRole.SYSTEM
        "assistant" -> AvRole.ASSISTANT
        "tool" -> AvRole.TOOL
        else -> AvRole.USER
    }

    private fun OpenAiUsage?.toDomain(): AvTokenUsage = this?.let {
        AvTokenUsage(
            promptTokens = it.promptTokens,
            completionTokens = it.completionTokens,
            totalTokens = it.totalTokens,
            cacheReadTokens = it.promptCacheHitTokens,
            cacheWriteTokens = it.promptCacheMissTokens,
        )
    } ?: AvTokenUsage(0, 0, 0)

    private fun Throwable.toApiError(provider: AvProvider): Throwable = when (this) {
        is HttpStatusException -> when (statusCode) {
            401, 403 -> AvApiError.AuthenticationFailed(provider.name)
            429 -> AvApiError.RateLimited(provider.name)
            else -> AvApiError.Unknown(provider.name, this)
        }

        else -> AvApiError.Network(provider.name, this)
    }

    private class HttpStatusException(
        val statusCode: Int,
        message: String,
    ) : Exception("HTTP $statusCode: $message")
}
