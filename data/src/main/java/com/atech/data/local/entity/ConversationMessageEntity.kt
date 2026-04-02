package com.atech.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "conversation_messages",
)
data class ConversationMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "conversation_id")
    val conversationId: String,
    val provider: String,
    @ColumnInfo(name = "model_id")
    val modelId: String,
    val role: String,
    val content: String,
    val name: String?,
    @ColumnInfo(name = "timestamp_epoch_ms")
    val timestampEpochMs: Long,
)