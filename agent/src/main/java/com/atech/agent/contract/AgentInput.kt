package com.atech.agent.contract

import com.atech.api_integration_common.model.AvModelConfig
import com.atech.api_integration_common.model.AvProvider

data class AgentInput(
    val conversationId: String,
    val provider: AvProvider,
    val modelId: String,
    val prompt: String,
    val modelConfig: AvModelConfig = AvModelConfig(),
    val memorySize: Int = 10,
    val onStreamDelta: (suspend (String) -> Unit)? = null,
)
