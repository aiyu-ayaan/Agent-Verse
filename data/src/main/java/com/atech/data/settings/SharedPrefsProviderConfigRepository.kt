package com.atech.data.settings

import android.content.Context
import androidx.core.content.edit
import com.atech.api_integration_common.contract.AvCredentialProvider
import com.atech.api_integration_common.model.AvProvider
import com.atech.api_integration_common.model.AvProviderCredentials
import com.atech.core.model.ProviderConfig
import com.atech.core.repository.ProviderConfigRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsProviderConfigRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ProviderConfigRepository, AvCredentialProvider {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override suspend fun saveConfig(config: ProviderConfig) {
        val key = keyPrefix(config.provider)
        prefs.edit {
            putString("${key}_api_key", config.apiKey)
            putString("${key}_base_url", config.baseUrl)
            putString("${key}_app_name", config.appName)
            putString("${key}_app_referer", config.appReferer)
            putBoolean("${key}_enabled", config.enabled)
        }
    }

    override suspend fun getConfig(provider: AvProvider): ProviderConfig? {
        return readConfig(provider)
    }

    private fun readConfig(provider: AvProvider): ProviderConfig? {
        val key = keyPrefix(provider)
        val apiKey = prefs.getString("${key}_api_key", null)?.trim().orEmpty()
        if (apiKey.isBlank()) {
            return null
        }

        return ProviderConfig(
            provider = provider,
            apiKey = apiKey,
            baseUrl = prefs.getString("${key}_base_url", null),
            appName = prefs.getString("${key}_app_name", null),
            appReferer = prefs.getString("${key}_app_referer", null),
            enabled = prefs.getBoolean("${key}_enabled", true),
        )
    }

    override fun observeConfigs(): Flow<List<ProviderConfig>> = callbackFlow {
        fun emitSnapshot() {
            trySend(AvProvider.entries.mapNotNull { readConfig(it) })
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

    override suspend fun getCredentials(provider: AvProvider): AvProviderCredentials? {
        val config = readConfig(provider) ?: return null
        if (!config.enabled || config.apiKey.isBlank()) {
            return null
        }

        return AvProviderCredentials(
            apiKey = config.apiKey,
            baseUrl = config.baseUrl,
            appName = config.appName,
            appReferer = config.appReferer,
        )
    }

    private fun keyPrefix(provider: AvProvider): String = provider.name.lowercase()

    private companion object {
        const val PREFS_NAME = "agentverse_provider_settings"
    }
}
