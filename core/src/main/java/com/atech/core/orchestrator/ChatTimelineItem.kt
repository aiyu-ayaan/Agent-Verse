package com.atech.core.orchestrator

import com.atech.api_integration_common.model.AvMessage

data class ChatTimelineItem(
    val id: Long,
    val conversationId: String,
    val role: String,
    val content: String,
    val timestampEpochMs: Long,
    val modelId: String
) {
    companion object {
        fun fromMessage(
            id: Long,
            conversationId: String,
            modelId: String,
            timestampEpochMs: Long,
            message: AvMessage
        ): ChatTimelineItem = ChatTimelineItem(
            id = id,
            conversationId = conversationId,
            role = message.role.name,
            content = message.content,
            timestampEpochMs = timestampEpochMs,
            modelId = modelId
        )
    }
}