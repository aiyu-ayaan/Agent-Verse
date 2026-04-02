package com.atech.api_integration_common.contract

import com.atech.api_integration_common.model.AvChatRequest
import com.atech.api_integration_common.model.AvChatResponse
import com.atech.api_integration_common.model.AvModelSummary
import com.atech.api_integration_common.model.AvProvider
import com.atech.api_integration_common.model.AvProviderCredentials

interface AvProviderConnector {
    val provider: AvProvider

    suspend fun chatCompletion(
        request: AvChatRequest,
        credentials: AvProviderCredentials
    ): Result<AvChatResponse>

    suspend fun listModels(credentials: AvProviderCredentials): Result<List<AvModelSummary>>
}