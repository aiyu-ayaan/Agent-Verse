package com.atech.api_integration_common.model

data class AvModelConfig(
    val temperature: Double? = null,
    val maxTokens: Int? = null,
    val topP: Double? = null,
    val frequencyPenalty: Double? = null,
    val presencePenalty: Double? = null,
    val stopSequences: List<String> = emptyList(),
    val stream: Boolean = false
)