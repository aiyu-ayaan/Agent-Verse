package com.atech.api_integration_common.model

data class AvChatRequest(
    val provider: AvProvider,
    val modelId: String,
    val messages: List<AvMessage>,
    val modelConfig: AvModelConfig = AvModelConfig(),
    val metadata: Map<String, String> = emptyMap()
)