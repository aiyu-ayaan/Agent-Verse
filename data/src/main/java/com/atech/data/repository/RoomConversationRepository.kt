package com.atech.data.repository

import com.atech.api_integration_common.model.AvMessage
import com.atech.api_integration_common.model.AvProvider
import com.atech.api_integration_common.model.AvRole
import com.atech.core.model.ConversationMessage
import com.atech.core.repository.ConversationRepository
import com.atech.data.local.dao.ConversationDao
import com.atech.data.local.entity.ConversationMessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomConversationRepository @Inject constructor(
    private val conversationDao: ConversationDao,
) : ConversationRepository {

    override suspend fun appendMessage(message: ConversationMessage) {
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