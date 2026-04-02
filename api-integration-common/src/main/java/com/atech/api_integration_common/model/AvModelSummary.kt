package com.atech.api_integration_common.model

data class AvModelSummary(
    val id: String,
    val provider: AvProvider,
    val ownedBy: String? = null,
    val contextWindow: Int? = null,
    val pricingHint: String? = null,
    val supportsTools: Boolean = false
)