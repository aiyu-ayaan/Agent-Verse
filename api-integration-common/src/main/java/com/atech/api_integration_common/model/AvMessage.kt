package com.atech.api_integration_common.model

data class AvMessage(
    val role: AvRole,
    val content: String,
    val name: String? = null
)