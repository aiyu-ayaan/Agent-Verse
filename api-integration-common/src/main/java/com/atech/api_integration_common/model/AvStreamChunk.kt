package com.atech.api_integration_common.model

data class AvStreamChunk(
    val contentDelta: String,
    val requestId: String? = null,
    val modelId: String? = null,
    val createdAtEpochMs: Long? = null,
    val finishReason: String? = null,
)
