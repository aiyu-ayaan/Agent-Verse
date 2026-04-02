package com.atech.agentverse.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.agent.contract.AgentInput
import com.atech.agent.registry.AgentRegistry
import com.atech.api_integration_common.model.AvModelConfig
import com.atech.api_integration_common.model.AvProvider
import com.atech.core.model.ChatSettings
import com.atech.core.model.ProviderConfig
import com.atech.core.orchestrator.AgentOrchestrator
import com.atech.core.repository.ChatSettingsRepository
import com.atech.core.repository.ConversationSessionRepository
import com.atech.core.repository.ProviderConfigRepository
import com.atech.core.repository.TokenUsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val agentRegistry: AgentRegistry,
    private val providerConfigRepository: ProviderConfigRepository,
    private val tokenUsageRepository: TokenUsageRepository,
    private val conversationSessionRepository: ConversationSessionRepository,
    private val chatSettingsRepository: ChatSettingsRepository,
    private val agentOrchestrator: AgentOrchestrator,
) : ViewModel() {

    private val selectedConversationId = MutableStateFlow<String?>(null)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeSessions()
        observeSelectedConversationMessages()
        observeUsage()
        observeChatSettings()
        refreshProviderConfig(_uiState.value.selectedProvider)
        ensureInitialConversation()
    }

    fun onOpenChatScreen() {
        _uiState.update { it.copy(activeScreen = ScreenDestination.CHAT) }
    }

    fun onOpenSettingsScreen() {
        _uiState.update { it.copy(activeScreen = ScreenDestination.SETTINGS) }
    }

    fun onSettingsTabSelected(tab: SettingsTab) {
        _uiState.update { it.copy(settingsTab = tab) }
    }

    fun onCreateNewChat() {
        viewModelScope.launch {
            val session = conversationSessionRepository.createSession()
            selectedConversationId.value = session.conversationId
            _uiState.update {
                it.copy(
                    activeScreen = ScreenDestination.CHAT,
                    selectedConversationId = session.conversationId,
                    messages = emptyList(),
                    prompt = "",
                    errorMessage = null,
                )
            }
        }
    }

    fun onConversationSelected(conversationId: String) {
        selectedConversationId.value = conversationId
        _uiState.update {
            it.copy(
                selectedConversationId = conversationId,
                activeScreen = ScreenDestination.CHAT,
                errorMessage = null,
            )
        }
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

    fun onMemorySizeChanged(value: Int) {
        val updated = _uiState.value.chatSettings.copy(memorySize = value)
        persistChatSettings(updated)
    }

    fun onStreamOutputChanged(enabled: Boolean) {
        val updated = _uiState.value.chatSettings.copy(streamOutput = enabled)
        persistChatSettings(updated)
    }

    fun resetTokenUsage() {
        viewModelScope.launch {
            tokenUsageRepository.clearAllUsage()
        }
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
            _uiState.update { it.copy(isSending = true, errorMessage = null, activeScreen = ScreenDestination.CHAT) }

            val conversationId = ensureConversationId()
            saveProviderConfigInternal(state)

            val result = agentRegistry.get("chat").execute(
                AgentInput(
                    conversationId = conversationId,
                    provider = state.selectedProvider,
                    modelId = state.modelId,
                    prompt = state.prompt,
                    modelConfig = AvModelConfig(
                        temperature = 0.3,
                        maxTokens = 1024,
                        topP = 0.9,
                        stream = state.chatSettings.streamOutput,
                    ),
                    memorySize = state.chatSettings.memorySize,
                ),
            )

            if (result.isSuccess) {
                maybeRenameSession(conversationId, state.prompt)
            }

            _uiState.update { current ->
                if (result.isSuccess) {
                    current.copy(
                        selectedConversationId = conversationId,
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

    private fun ensureInitialConversation() {
        viewModelScope.launch {
            ensureConversationId()
        }
    }

    private suspend fun ensureConversationId(): String {
        selectedConversationId.value?.let { return it }
        val created = conversationSessionRepository.createSession()
        selectedConversationId.value = created.conversationId
        _uiState.update { it.copy(selectedConversationId = created.conversationId) }
        return created.conversationId
    }

    private suspend fun maybeRenameSession(conversationId: String, prompt: String) {
        val existing = conversationSessionRepository.getSession(conversationId) ?: return
        if (existing.title != "New Chat") {
            return
        }

        val title = prompt
            .lineSequence()
            .firstOrNull()
            .orEmpty()
            .trim()
            .take(48)
            .ifBlank { "New Chat" }

        conversationSessionRepository.updateSessionTitle(conversationId, title)
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

    private fun observeSessions() {
        viewModelScope.launch {
            conversationSessionRepository.observeSessions().collect { sessions ->
                val currentSelected = selectedConversationId.value
                val fallbackSelection = currentSelected
                    ?.takeIf { id -> sessions.any { it.conversationId == id } }
                    ?: sessions.firstOrNull()?.conversationId

                selectedConversationId.value = fallbackSelection
                _uiState.update {
                    it.copy(
                        conversations = sessions,
                        selectedConversationId = fallbackSelection,
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSelectedConversationMessages() {
        viewModelScope.launch {
            selectedConversationId
                .flatMapLatest { conversationId ->
                    if (conversationId == null) {
                        flowOf(emptyList())
                    } else {
                        agentOrchestrator.observeConversation(conversationId)
                    }
                }
                .collect { messages ->
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

    private fun observeChatSettings() {
        viewModelScope.launch {
            chatSettingsRepository.observeSettings().collect { settings ->
                _uiState.update { it.copy(chatSettings = settings) }
            }
        }
    }

    private fun persistChatSettings(settings: ChatSettings) {
        viewModelScope.launch {
            chatSettingsRepository.saveSettings(settings)
            _uiState.update { it.copy(chatSettings = settings) }
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
