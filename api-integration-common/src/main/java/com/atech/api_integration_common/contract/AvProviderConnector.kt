package com.atech.api_integration_common.contract

import com.atech.api_integration_common.model.AvChatRequest
import com.atech.api_integration_common.model.AvChatResponse
import com.atech.api_integration_common.model.AvModelSummary
import com.atech.api_integration_common.model.AvProvider
import com.atech.api_integration_common.model.AvProviderCredentials
import com.atech.api_integration_common.model.AvStreamChunk

interface AvProviderConnector {
    val provider: AvProvider

    suspend fun chatCompletion(
        request: AvChatRequest,
        credentials: AvProviderCredentials,
        onChunk: suspend (AvStreamChunk) -> Unit = {},
    ): Result<AvChatResponse>

    suspend fun listModels(credentials: AvProviderCredentials): Result<List<AvModelSummary>>
}
