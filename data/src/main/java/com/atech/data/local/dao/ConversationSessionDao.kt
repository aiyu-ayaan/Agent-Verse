package com.atech.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atech.data.local.entity.ConversationSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ConversationSessionEntity)

    @Query(
        "SELECT * FROM conversation_sessions WHERE conversation_id = :conversationId LIMIT 1",
    )
    suspend fun getById(conversationId: String): ConversationSessionEntity?

    @Query(
        "SELECT * FROM conversation_sessions ORDER BY updated_at_epoch_ms DESC",
    )
    fun observeSessions(): Flow<List<ConversationSessionEntity>>
}