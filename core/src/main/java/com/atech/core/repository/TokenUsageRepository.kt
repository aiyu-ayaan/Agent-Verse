package com.atech.core.repository

import com.atech.api_integration_common.model.AvProvider
import com.atech.api_integration_common.model.AvTokenUsage
import com.atech.core.model.ProviderUsageSummary
import kotlinx.coroutines.flow.Flow

interface TokenUsageRepository {
    suspend fun recordUsage(
        conversationId: String,
        provider: AvProvider,
        modelId: String,
        usage: AvTokenUsage,
        timestampEpochMs: Long
    )

    fun observeProviderUsage(): Flow<List<ProviderUsageSummary>>

    suspend fun clearAllUsage()
}
