package com.atech.agentverse.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.agent.contract.AgentInput
import com.atech.agent.registry.AgentRegistry
import com.atech.api_integration_common.model.AvModelConfig
import com.atech.api_integration_common.model.AvProvider
import com.atech.core.model.ProviderConfig
import com.atech.core.orchestrator.AgentOrchestrator
import com.atech.core.repository.ProviderConfigRepository
import com.atech.core.repository.TokenUsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val agentRegistry: AgentRegistry,
    private val providerConfigRepository: ProviderConfigRepository,
    private val tokenUsageRepository: TokenUsageRepository,
    private val agentOrchestrator: AgentOrchestrator,
) : ViewModel() {

    private val conversationId = UUID.randomUUID().toString()

    private val _uiState = MutableStateFlow(MainUiState(conversationId = conversationId))
    val uiState = _uiState.asStateFlow()

    init {
        observeConversation()
        observeUsage()
        refreshProviderConfig(_uiState.value.selectedProvider)
    }

    fun onProviderSelected(provider: AvProvider) {
        _uiState.update {
            it.copy(
                selectedProvider = provider,
                modelId = defaultModelFor(provider),
                errorMessage = null,
            )
        }
        refreshProviderConfig(provider)
    }

    fun onModelIdChanged(value: String) {
        _uiState.update { it.copy(modelId = value) }
    }

    fun onPromptChanged(value: String) {
        _uiState.update { it.copy(prompt = value) }
    }

    fun onApiKeyChanged(value: String) {
        _uiState.update { it.copy(apiKey = value) }
    }

    fun onBaseUrlChanged(value: String) {
        _uiState.update { it.copy(baseUrl = value) }
    }

    fun onAppNameChanged(value: String) {
        _uiState.update { it.copy(appName = value) }
    }

    fun onAppRefererChanged(value: String) {
        _uiState.update { it.copy(appReferer = value) }
    }

    fun saveProviderConfig() {
        val state = _uiState.value
        viewModelScope.launch {
            saveProviderConfigInternal(state)
            _uiState.update { it.copy(errorMessage = null) }
        }
    }

    fun sendPrompt() {
        val state = _uiState.value
        if (state.prompt.isBlank()) {
            return
        }
        if (state.apiKey.isBlank()) {
            _uiState.update { it.copy(errorMessage = "API key is required for ${state.selectedProvider.name}.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true, errorMessage = null) }
            saveProviderConfigInternal(state)

            val result = agentRegistry.get("chat").execute(
                AgentInput(
                    conversationId = state.conversationId,
                    provider = state.selectedProvider,
                    modelId = state.modelId,
                    prompt = state.prompt,
                    modelConfig = AvModelConfig(
                        temperature = 0.3,
                        maxTokens = 1024,
                        topP = 0.9,
                    ),
                    memorySize = 10,
                ),
            )

            _uiState.update { current ->
                if (result.isSuccess) {
                    current.copy(
                        prompt = "",
                        isSending = false,
                        errorMessage = null,
                    )
                } else {
                    current.copy(
                        isSending = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Failed to send prompt",
                    )
                }
            }
        }
    }

    private fun refreshProviderConfig(provider: AvProvider) {
        viewModelScope.launch {
            providerConfigRepository.getConfig(provider)?.let { config ->
                _uiState.update {
                    it.copy(
                        apiKey = config.apiKey,
                        baseUrl = config.baseUrl.orEmpty(),
                        appName = config.appName.orEmpty().ifBlank { "AgentVerse" },
                        appReferer = config.appReferer.orEmpty(),
                    )
                }
            }
        }
    }

    private fun observeConversation() {
        viewModelScope.launch {
            agentOrchestrator.observeConversation(conversationId).collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    private fun observeUsage() {
        viewModelScope.launch {
            tokenUsageRepository.observeProviderUsage().collect { usage ->
                _uiState.update { it.copy(providerUsage = usage) }
            }
        }
    }

    private suspend fun saveProviderConfigInternal(state: MainUiState) {
        providerConfigRepository.saveConfig(
            ProviderConfig(
                provider = state.selectedProvider,
                apiKey = state.apiKey.trim(),
                baseUrl = state.baseUrl.trim().takeIf { it.isNotBlank() },
                appName = state.appName.trim().takeIf { it.isNotBlank() },
                appReferer = state.appReferer.trim().takeIf { it.isNotBlank() },
                enabled = true,
            ),
        )
    }

    private fun defaultModelFor(provider: AvProvider): String = when (provider) {
        AvProvider.GROQ -> MainUiState.DEFAULT_GROQ_MODEL
        AvProvider.OPENROUTER -> MainUiState.DEFAULT_OPENROUTER_MODEL
    }
}