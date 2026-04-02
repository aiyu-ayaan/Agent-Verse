package com.atech.agentverse.presentation

import com.atech.api_integration_common.model.AvProvider
import com.atech.core.model.ProviderUsageSummary
import com.atech.core.orchestrator.ChatTimelineItem

data class MainUiState(
    val conversationId: String,
    val selectedProvider: AvProvider = AvProvider.GROQ,
    val modelId: String = DEFAULT_GROQ_MODEL,
    val prompt: String = "",
    val apiKey: String = "",
    val baseUrl: String = "",
    val appName: String = "AgentVerse",
    val appReferer: String = "",
    val isSending: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<ChatTimelineItem> = emptyList(),
    val providerUsage: List<ProviderUsageSummary> = emptyList(),
) {
    companion object {
        const val DEFAULT_GROQ_MODEL: String = "llama-3.3-70b-versatile"
        const val DEFAULT_OPENROUTER_MODEL: String = "openai/gpt-4o-mini"
    }
}