package com.atech.api_integration_common.contract

import com.atech.api_integration_common.model.AvChatRequest
import com.atech.api_integration_common.model.AvChatResponse
import com.atech.api_integration_common.model.AvModelSummary
import com.atech.api_integration_common.model.AvProvider

interface AvProviderGateway {
    suspend fun chatCompletion(request: AvChatRequest): Result<AvChatResponse>
    suspend fun listModels(provider: AvProvider): Result<List<AvModelSummary>>
}