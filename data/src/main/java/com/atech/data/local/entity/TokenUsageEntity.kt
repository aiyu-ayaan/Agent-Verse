package com.atech.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "token_usage",
)
data class TokenUsageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "conversation_id")
    val conversationId: String,
    val provider: String,
    @ColumnInfo(name = "model_id")
    val modelId: String,
    @ColumnInfo(name = "prompt_tokens")
    val promptTokens: Int,
    @ColumnInfo(name = "completion_tokens")
    val completionTokens: Int,
    @ColumnInfo(name = "total_tokens")
    val totalTokens: Int,
    @ColumnInfo(name = "cache_read_tokens")
    val cacheReadTokens: Int,
    @ColumnInfo(name = "cache_write_tokens")
    val cacheWriteTokens: Int,
    @ColumnInfo(name = "timestamp_epoch_ms")
    val timestampEpochMs: Long,
)

data class ProviderUsageAggregate(
    @ColumnInfo(name = "provider_name")
    val providerName: String,
    @ColumnInfo(name = "total_prompt_tokens")
    val totalPromptTokens: Long,
    @ColumnInfo(name = "total_completion_tokens")
    val totalCompletionTokens: Long,
    @ColumnInfo(name = "total_tokens")
    val totalTokens: Long,
    @ColumnInfo(name = "total_requests")
    val totalRequests: Long,
)