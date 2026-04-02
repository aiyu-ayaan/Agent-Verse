package com.atech.api_integration_common.model

data class AvChatResponse(
    val requestId: String,
    val provider: AvProvider,
    val modelId: String,
    val createdAtEpochMs: Long,
    val message: AvMessage,
    val finishReason: String? = null,
    val usage: AvTokenUsage = AvTokenUsage(0, 0, 0),
    val rawResponse: String? = null
)