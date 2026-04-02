package com.atech.api_integration_common.model

data class AvTokenUsage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int,
    val cacheReadTokens: Int = 0,
    val cacheWriteTokens: Int = 0
)