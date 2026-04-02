package com.atech.api_integration_common.model

enum class AvProvider {
    GROQ,
    OPENROUTER;

    companion object {
        fun fromValue(value: String): AvProvider =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
                ?: error("Unsupported provider: $value")
    }
}