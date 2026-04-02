package com.atech.core.repository

import com.atech.core.model.ChatSettings
import kotlinx.coroutines.flow.Flow

interface ChatSettingsRepository {
    fun observeSettings(): Flow<ChatSettings>
    suspend fun getSettings(): ChatSettings
    suspend fun saveSettings(settings: ChatSettings)
}