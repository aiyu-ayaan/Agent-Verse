package com.atech.core.model

data class ChatSettings(
    val memorySize: Int = 10,
    val streamOutput: Boolean = false,
)