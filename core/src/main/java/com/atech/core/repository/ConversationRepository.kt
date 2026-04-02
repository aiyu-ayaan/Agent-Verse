package com.atech.core.repository

import com.atech.core.model.ConversationMessage
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    suspend fun appendMessage(message: ConversationMessage)
    fun observeConversation(conversationId: String): Flow<List<ConversationMessage>>
    suspend fun getRecentMessages(conversationId: String, limit: Int): List<ConversationMessage>
}