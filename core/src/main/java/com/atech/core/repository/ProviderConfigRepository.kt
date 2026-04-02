package com.atech.core.repository

import com.atech.api_integration_common.model.AvProvider
import com.atech.core.model.ProviderConfig
import kotlinx.coroutines.flow.Flow

interface ProviderConfigRepository {
    suspend fun saveConfig(config: ProviderConfig)
    suspend fun getConfig(provider: AvProvider): ProviderConfig?
    fun observeConfigs(): Flow<List<ProviderConfig>>
}