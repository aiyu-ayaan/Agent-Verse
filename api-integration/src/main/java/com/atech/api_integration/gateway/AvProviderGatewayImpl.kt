package com.atech.api_integration.gateway

import com.atech.api_integration.connector.GroqConnector
import com.atech.api_integration.connector.OpenRouterConnector
import com.atech.api_integration_common.contract.AvCredentialProvider
import com.atech.api_integration_common.contract.AvProviderGateway
import com.atech.api_integration_common.error.AvApiError
import com.atech.api_integration_common.model.AvChatRequest
import com.atech.api_integration_common.model.AvChatResponse
import com.atech.api_integration_common.model.AvModelSummary
import com.atech.api_integration_common.model.AvProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AvProviderGatewayImpl @Inject constructor(
    private val credentialProvider: AvCredentialProvider,
    private val groqConnector: GroqConnector,
    private val openRouterConnector: OpenRouterConnector,
) : AvProviderGateway {

    override suspend fun chatCompletion(request: AvChatRequest): Result<AvChatResponse> {
        val credentials = credentialProvider.getCredentials(request.provider)
            ?: return Result.failure(AvApiError.MissingCredentials(request.provider.name))

        return connectorFor(request.provider).chatCompletion(request, credentials)
    }

    override suspend fun listModels(provider: AvProvider): Result<List<AvModelSummary>> {
        val credentials = credentialProvider.getCredentials(provider)
            ?: return Result.failure(AvApiError.MissingCredentials(provider.name))

        return connectorFor(provider).listModels(credentials)
    }

    private fun connectorFor(provider: AvProvider) = when (provider) {
        AvProvider.GROQ -> groqConnector
        AvProvider.OPENROUTER -> openRouterConnector
    }
}