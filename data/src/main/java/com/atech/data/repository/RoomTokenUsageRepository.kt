package com.atech.data.repository

import com.atech.api_integration_common.model.AvProvider
import com.atech.api_integration_common.model.AvTokenUsage
import com.atech.core.model.ProviderUsageSummary
import com.atech.core.repository.TokenUsageRepository
import com.atech.data.local.dao.TokenUsageDao
import com.atech.data.local.entity.TokenUsageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomTokenUsageRepository @Inject constructor(
    private val tokenUsageDao: TokenUsageDao,
) : TokenUsageRepository {

    override suspend fun recordUsage(
        conversationId: String,
        provider: AvProvider,
        modelId: String,
        usage: AvTokenUsage,
        timestampEpochMs: Long,
    ) {
        tokenUsageDao.insert(
            TokenUsageEntity(
                conversationId = conversationId,
                provider = provider.name,
                modelId = modelId,
                promptTokens = usage.promptTokens,
                completionTokens = usage.completionTokens,
                totalTokens = usage.totalTokens,
                cacheReadTokens = usage.cacheReadTokens,
                cacheWriteTokens = usage.cacheWriteTokens,
                timestampEpochMs = timestampEpochMs,
            ),
        )
    }

    override fun observeProviderUsage(): Flow<List<ProviderUsageSummary>> =
        tokenUsageDao.observeProviderUsage().map { rows ->
            rows.map { row ->
                ProviderUsageSummary(
                    provider = AvProvider.fromValue(row.providerName),
                    totalPromptTokens = row.totalPromptTokens,
                    totalCompletionTokens = row.totalCompletionTokens,
                    totalTokens = row.totalTokens,
                    totalRequests = row.totalRequests,
                )
            }
        }

    override suspend fun clearAllUsage() {
        tokenUsageDao.clearAll()
    }
}
