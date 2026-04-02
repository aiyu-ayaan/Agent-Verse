package com.atech.core.model

data class ConversationSession(
    val conversationId: String,
    val title: String,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long,
)