package com.atech.core.repository

import com.atech.core.model.ConversationSession
import kotlinx.coroutines.flow.Flow

interface ConversationSessionRepository {
    suspend fun createSession(title: String? = null): ConversationSession
    suspend fun updateSessionTitle(conversationId: String, title: String)
    suspend fun touchSession(conversationId: String)
    fun observeSessions(): Flow<List<ConversationSession>>
    suspend fun getSession(conversationId: String): ConversationSession?
}