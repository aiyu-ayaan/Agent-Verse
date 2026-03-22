/*
 *
 *  Copyright (c) 2026 Ayaan.
 *  Licensed under the MIT License.
 *
 *  Created: 2026
 *  Modified: 3/22/26, 1:38 PM
 *
 *  AgentVerse
 *  Integrates multiple AI models with a modular clean architecture.
 *
 *
 */

package com.atech.api_integration_common.model

import kotlinx.serialization.Serializable

/**
 * Represents the output settings for an AI model response in the AgentVerse system,
 * allowing for configuration of how the output should be generated and delivered.
 * This structure provides a way to specify whether the response should be streamed in real-time or delivered as a complete output, enabling more flexible and efficient handling of responses based on the specific requirements of the application or use case.
 * @param stream A boolean flag indicating whether the response should be streamed in real-time (true) or delivered as a complete output (false).
 * @see AvModelConfig
 * @see AvRequest
 * @see AvMessage
 */
@Serializable
data class OutputSettings(
    val stream: Boolean = false
)