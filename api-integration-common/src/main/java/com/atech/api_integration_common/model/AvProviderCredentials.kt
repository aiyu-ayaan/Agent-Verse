package com.atech.api_integration_common.model

data class AvProviderCredentials(
    val apiKey: String,
    val baseUrl: String? = null,
    val appName: String? = null,
    val appReferer: String? = null
)