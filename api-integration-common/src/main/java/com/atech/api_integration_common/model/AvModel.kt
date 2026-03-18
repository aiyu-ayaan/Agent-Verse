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
 * Represents a model available in the AgentVerse system,
 * including its metadata and capabilities.
 */
@Keep
@Serializable
data class AvModel(
    val modelId: String,
    val ownedBy: String,
    val isActive: Boolean,
    val contextWindow: Int,
    val created: Long
)