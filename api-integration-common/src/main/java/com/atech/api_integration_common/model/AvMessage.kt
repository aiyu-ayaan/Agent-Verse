/*
 *
 *  Copyright (c) 2026 Ayaan.
 *  Licensed under the MIT License.
 *
 *  Created: 2026
 *  Modified: 3/18/26, 10:18 PM
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
 * Defines the role of the
 * message sender in the conversation.
 * - USER: The end-user interacting with the system.
 * - ASSISTANT: The AI assistant responding to the user.
 * - SYSTEM: System-level messages, such as instructions or metadata.
 */
enum class Role{
    USER,
    ASSISTANT,
    SYSTEM
}

/**
 * Represents a single message in the conversation, including the sender's role and the content of the message.
 * This structure allows for a clear distinction between different types of messages in the dialogue, facilitating better context management and response generation.
 * @param role The role of the message sender (USER, ASSISTANT, SYSTEM).
 * @param content The actual text content of the message.
 * @see Role
 */
@Keep
@Serializable
data class AvMessage(
    val role: Role,
    val content: String
)