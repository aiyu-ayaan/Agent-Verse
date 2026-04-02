package com.atech.core.orchestrator

import com.atech.api_integration_common.model.AvChatRequest
import com.atech.api_integration_common.model.AvChatResponse
import kotlinx.coroutines.flow.Flow

interface AgentOrchestrator {
    suspend fun executeChat(request: AvChatRequest, conversationId: String): Result<AvChatResponse>
    fun observeConversation(conversationId: String): Flow<List<ChatTimelineItem>>
}