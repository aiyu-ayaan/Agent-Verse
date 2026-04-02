package com.atech.core.model

import com.atech.api_integration_common.model.AvMessage
import com.atech.api_integration_common.model.AvProvider

data class ConversationMessage(
    val id: Long = 0,
    val conversationId: String,
    val provider: AvProvider,
    val modelId: String,
    val message: AvMessage,
    val timestampEpochMs: Long
)