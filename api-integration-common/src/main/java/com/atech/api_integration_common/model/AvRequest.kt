/*
 *
 *  Copyright (c) 2026 Ayaan.
 *  Licensed under the MIT License.
 *
 *  Created: 2026
 *  Modified: 3/18/26, 9:46 PM
 *
 *  AgentVerse
 *  Integrates multiple AI models with a modular clean architecture.
 *
 *
 */

package com.atech.api_integration_common.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

/**
 * Represents a request to the AgentVerse system,
 * containing the necessary information.
 * model to be used for processing the request and a list of messages that form the conversation context.
 * This structure allows for a clear and organized way to send requests to the system, ensuring that
 * the appropriate model is utilized and that the conversation history is maintained for accurate response generation.
 * @param model The AI model to be used for processing the request.
 * @param message A list of messages that form the conversation context, including the role of the
 * @param modelConfig The configuration settings for the model, allowing for fine-tuning of the response generation process based on specific requirements.
 * @see AvModel
 * @see AvMessage
 * @see AvModelConfig
 */
@Keep
@Serializable
data class AvRequest(
    val model : AvModel,
    val message : List<AvMessage>,
    val modelConfig: AvModelConfig = AvModelConfig()
)


