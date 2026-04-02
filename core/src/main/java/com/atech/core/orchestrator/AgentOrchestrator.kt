package com.atech.core.orchestrator

import com.atech.api_integration_common.model.AvModelConfig
import com.atech.api_integration_common.model.AvProvider
import com.atech.api_integration_common.model.AvChatRequest
import com.atech.api_integration_common.model.AvChatResponse
import kotlinx.coroutines.flow.Flow

interface AgentOrchestrator {
    suspend fun executeChat(request: AvChatRequest, conversationId: String): Result<AvChatResponse>
    suspend fun sendUserPrompt(
        conversationId: String,
        provider: AvProvider,
        modelId: String,
        prompt: String,
        systemPrompt: String? = null,
        modelConfig: AvModelConfig = AvModelConfig(),
        memorySize: Int = 10
    ): Result<AvChatResponse>
    fun observeConversation(conversationId: String): Flow<List<ChatTimelineItem>>
}
