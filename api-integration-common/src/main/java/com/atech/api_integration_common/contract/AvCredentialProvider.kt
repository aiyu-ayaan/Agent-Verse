package com.atech.api_integration_common.contract

import com.atech.api_integration_common.model.AvProvider
import com.atech.api_integration_common.model.AvProviderCredentials

interface AvCredentialProvider {
    suspend fun getCredentials(provider: AvProvider): AvProviderCredentials?
}