package com.atech.data.repository

import com.atech.core.model.ConversationSession
import com.atech.core.repository.ConversationSessionRepository
import com.atech.data.local.dao.ConversationSessionDao
import com.atech.data.local.entity.ConversationSessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomConversationSessionRepository @Inject constructor(
    private val sessionDao: ConversationSessionDao,
) : ConversationSessionRepository {

    override suspend fun createSession(title: String?): ConversationSession {
        val now = System.currentTimeMillis()
        val session = ConversationSessionEntity(
            conversationId = UUID.randomUUID().toString(),
            title = title?.trim().orEmpty().ifBlank { "New Chat" },
            createdAtEpochMs = now,
            updatedAtEpochMs = now,
        )
        sessionDao.upsert(session)
        return session.toDomain()
    }

    override suspend fun updateSessionTitle(conversationId: String, title: String) {
        val existing = sessionDao.getById(conversationId) ?: return
        sessionDao.upsert(
            existing.copy(
                title = title.trim().ifBlank { existing.title },
                updatedAtEpochMs = System.currentTimeMillis(),
            ),
        )
    }

    override suspend fun touchSession(conversationId: String) {
        val existing = sessionDao.getById(conversationId) ?: return
        sessionDao.upsert(
            existing.copy(
                updatedAtEpochMs = System.currentTimeMillis(),
            ),
        )
    }

    override fun observeSessions(): Flow<List<ConversationSession>> =
        sessionDao.observeSessions().map { list -> list.map { it.toDomain() } }

    override suspend fun getSession(conversationId: String): ConversationSession? =
        sessionDao.getById(conversationId)?.toDomain()

    private fun ConversationSessionEntity.toDomain(): ConversationSession = ConversationSession(
        conversationId = conversationId,
        title = title,
        createdAtEpochMs = createdAtEpochMs,
        updatedAtEpochMs = updatedAtEpochMs,
    )
}