package com.atech.core.orchestrator

import com.atech.api_integration_common.contract.AvProviderGateway
import com.atech.api_integration_common.model.AvChatRequest
import com.atech.api_integration_common.model.AvChatResponse
import com.atech.api_integration_common.model.AvMessage
import com.atech.api_integration_common.model.AvModelConfig
import com.atech.api_integration_common.model.AvProvider
import com.atech.api_integration_common.model.AvRole
import com.atech.core.model.ConversationMessage
import com.atech.core.repository.ConversationRepository
import com.atech.core.repository.TokenUsageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentOrchestratorImpl @Inject constructor(
    private val providerGateway: AvProviderGateway,
    private val conversationRepository: ConversationRepository,
    private val tokenUsageRepository: TokenUsageRepository,
) : AgentOrchestrator {

    override suspend fun executeChat(
        request: AvChatRequest,
        conversationId: String,
    ): Result<AvChatResponse> {
        val timestamp = System.currentTimeMillis()
        request.messages.lastOrNull { it.role == AvRole.USER }?.let { userMessage ->
            conversationRepository.appendMessage(
                ConversationMessage(
                    conversationId = conversationId,
                    provider = request.provider,
                    modelId = request.modelId,
                    message = userMessage,
                    timestampEpochMs = timestamp,
                )
            )
        }

        return providerGateway.chatCompletion(request).onSuccess { response ->
            conversationRepository.appendMessage(
                ConversationMessage(
                    conversationId = conversationId,
                    provider = response.provider,
                    modelId = response.modelId,
                    message = response.message,
                    timestampEpochMs = response.createdAtEpochMs,
                )
            )
            tokenUsageRepository.recordUsage(
                conversationId = conversationId,
                provider = response.provider,
                modelId = response.modelId,
                usage = response.usage,
                timestampEpochMs = response.createdAtEpochMs,
            )
        }
    }

    override suspend fun sendUserPrompt(
        conversationId: String,
        provider: AvProvider,
        modelId: String,
        prompt: String,
        systemPrompt: String?,
        modelConfig: AvModelConfig,
        memorySize: Int,
    ): Result<AvChatResponse> {
        val history = conversationRepository.getRecentMessages(conversationId, memorySize)
            .map { it.message }
            .takeLast(memorySize)

        val payload = buildList {
            if (!systemPrompt.isNullOrBlank()) {
                add(AvMessage(role = AvRole.SYSTEM, content = systemPrompt))
            }
            addAll(history)
            add(AvMessage(role = AvRole.USER, content = prompt))
        }

        val request = AvChatRequest(
            provider = provider,
            modelId = modelId,
            messages = payload,
            modelConfig = modelConfig,
        )

        return executeChat(request, conversationId)
    }

    override fun observeConversation(conversationId: String): Flow<List<ChatTimelineItem>> =
        conversationRepository.observeConversation(conversationId).map { messages ->
            messages.map { entry ->
                ChatTimelineItem.fromMessage(
                    id = entry.id,
                    conversationId = entry.conversationId,
                    modelId = entry.modelId,
                    timestampEpochMs = entry.timestampEpochMs,
                    message = entry.message,
                )
            }
        }
}