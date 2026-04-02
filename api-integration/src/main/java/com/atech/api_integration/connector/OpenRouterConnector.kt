package com.atech.api_integration.connector

import com.atech.api_integration_common.model.AvProvider
import com.atech.api_integration_common.model.AvProviderCredentials
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenRouterConnector @Inject constructor(
    okHttpClient: OkHttpClient,
    json: Json,
) : OpenAiCompatibleConnector(okHttpClient, json) {

    override val provider: AvProvider = AvProvider.OPENROUTER

    override val defaultBaseUrl: String = "https://openrouter.ai/api/v1/"

    override fun buildHeaders(credentials: AvProviderCredentials): Map<String, String> = mapOf(
        "Authorization" to "Bearer ${credentials.apiKey}",
        "HTTP-Referer" to (credentials.appReferer ?: "https://agentverse.app"),
        "X-Title" to (credentials.appName ?: "AgentVerse"),
        "Content-Type" to "application/json",
    )
}