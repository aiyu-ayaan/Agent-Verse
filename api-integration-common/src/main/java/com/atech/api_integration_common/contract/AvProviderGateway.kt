package com.atech.api_integration_common.contract

import com.atech.api_integration_common.model.AvChatRequest
import com.atech.api_integration_common.model.AvChatResponse
import com.atech.api_integration_common.model.AvModelSummary
import com.atech.api_integration_common.model.AvProvider
import com.atech.api_integration_common.model.AvStreamChunk

interface AvProviderGateway {
    suspend fun chatCompletion(
        request: AvChatRequest,
        onChunk: suspend (AvStreamChunk) -> Unit = {},
    ): Result<AvChatResponse>

    suspend fun listModels(provider: AvProvider): Result<List<AvModelSummary>>
}
