package com.atech.api_integration_common.error

sealed class AvApiError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class MissingCredentials(provider: String) : AvApiError("Missing credentials for provider: $provider")
    class AuthenticationFailed(provider: String) : AvApiError("Authentication failed for provider: $provider")
    class RateLimited(provider: String) : AvApiError("Rate limit exceeded for provider: $provider")
    class Network(provider: String, cause: Throwable) : AvApiError("Network error for provider: $provider", cause)
    class Unknown(provider: String, cause: Throwable? = null) : AvApiError("Unexpected error for provider: $provider", cause)
}