package com.atech.core.model

import com.atech.api_integration_common.model.AvProvider

data class ProviderConfig(
    val provider: AvProvider,
    val apiKey: String,
    val baseUrl: String? = null,
    val appName: String? = null,
    val appReferer: String? = null,
    val enabled: Boolean = true,
)