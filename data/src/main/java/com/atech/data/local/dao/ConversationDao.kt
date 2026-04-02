package com.atech.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atech.data.local.entity.ConversationMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ConversationMessageEntity)

    @Query(
        "SELECT * FROM conversation_messages WHERE conversation_id = :conversationId ORDER BY timestamp_epoch_ms ASC, id ASC",
    )
    fun observeConversation(conversationId: String): Flow<List<ConversationMessageEntity>>

    @Query(
        "SELECT * FROM conversation_messages WHERE conversation_id = :conversationId ORDER BY timestamp_epoch_ms DESC, id DESC LIMIT :limit",
    )
    suspend fun getRecentMessages(conversationId: String, limit: Int): List<ConversationMessageEntity>
}