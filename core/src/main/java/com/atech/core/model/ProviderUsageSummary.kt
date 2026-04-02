package com.atech.core.model

import com.atech.api_integration_common.model.AvProvider

data class ProviderUsageSummary(
    val provider: AvProvider,
    val totalPromptTokens: Long,
    val totalCompletionTokens: Long,
    val totalTokens: Long,
    val totalRequests: Long
)