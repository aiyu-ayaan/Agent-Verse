package com.atech.data.settings

import android.content.Context
import androidx.core.content.edit
import com.atech.core.model.ChatSettings
import com.atech.core.repository.ChatSettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsChatSettingsRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ChatSettingsRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun observeSettings(): Flow<ChatSettings> = callbackFlow {
        fun emitSnapshot() {
            trySend(readSettings())
        }

        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            emitSnapshot()
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)
        emitSnapshot()

        awaitClose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    override suspend fun getSettings(): ChatSettings = readSettings()

    override suspend fun saveSettings(settings: ChatSettings) {
        prefs.edit {
            putInt(KEY_MEMORY_SIZE, settings.memorySize.coerceIn(2, 50))
            putBoolean(KEY_STREAM_OUTPUT, settings.streamOutput)
        }
    }

    private fun readSettings(): ChatSettings = ChatSettings(
        memorySize = prefs.getInt(KEY_MEMORY_SIZE, 10).coerceIn(2, 50),
        streamOutput = prefs.getBoolean(KEY_STREAM_OUTPUT, false),
    )

    private companion object {
        const val PREFS_NAME = "agentverse_chat_settings"
        const val KEY_MEMORY_SIZE = "memory_size"
        const val KEY_STREAM_OUTPUT = "stream_output"
    }
}