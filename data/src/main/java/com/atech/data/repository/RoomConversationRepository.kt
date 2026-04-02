package com.atech.data.repository

import com.atech.api_integration_common.model.AvMessage
import com.atech.api_integration_common.model.AvProvider
import com.atech.api_integration_common.model.AvRole
import com.atech.core.model.ConversationMessage
import com.atech.core.repository.ConversationRepository
import com.atech.data.local.dao.ConversationDao
import com.atech.data.local.dao.ConversationSessionDao
import com.atech.data.local.entity.ConversationMessageEntity
import com.atech.data.local.entity.ConversationSessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomConversationRepository @Inject constructor(
    private val conversationDao: ConversationDao,
    private val sessionDao: ConversationSessionDao,
) : ConversationRepository {

    override suspend fun appendMessage(message: ConversationMessage) {
        val now = System.currentTimeMillis()
        val existingSession = sessionDao.getById(message.conversationId)
        val defaultTitle = message.message.content
            .lineSequence()
            .firstOrNull()
            .orEmpty()
            .trim()
            .take(48)
            .ifBlank { "New Chat" }

        if (existingSession == null) {
            sessionDao.upsert(
                ConversationSessionEntity(
                    conversationId = message.conversationId,
                    title = defaultTitle,
                    createdAtEpochMs = now,
                    updatedAtEpochMs = now,
                ),
            )
        } else {
            sessionDao.upsert(
                existingSession.copy(
                    title = if (existingSession.title == "New Chat" && message.message.role == AvRole.USER) {
                        defaultTitle
                    } else {
                        existingSession.title
                    },
                    updatedAtEpochMs = now,
                ),
            )
        }

        conversationDao.insert(message.toEntity())
    }

    override fun observeConversation(conversationId: String): Flow<List<ConversationMessage>> =
        conversationDao.observeConversation(conversationId).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun getRecentMessages(conversationId: String, limit: Int): List<ConversationMessage> =
        conversationDao.getRecentMessages(conversationId, limit)
            .asReversed()
            .map { it.toDomain() }

    private fun ConversationMessage.toEntity(): ConversationMessageEntity = ConversationMessageEntity(
        id = id,
        conversationId = conversationId,
        provider = provider.name,
        modelId = modelId,
        role = message.role.name,
        content = message.content,
        name = message.name,
        timestampEpochMs = timestampEpochMs,
    )

    private fun ConversationMessageEntity.toDomain(): ConversationMessage = ConversationMessage(
        id = id,
        conversationId = conversationId,
        provider = AvProvider.fromValue(provider),
        modelId = modelId,
        message = AvMessage(
            role = AvRole.valueOf(role),
            content = content,
            name = name,
        ),
        timestampEpochMs = timestampEpochMs,
    )
}
