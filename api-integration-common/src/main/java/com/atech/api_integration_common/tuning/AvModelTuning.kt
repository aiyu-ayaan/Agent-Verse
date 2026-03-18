/*
 *
 *  Copyright (c) 2026 Ayaan.
 *  Licensed under the MIT License.
 *
 *  Created: 2026
 *  Modified: 3/18/26, 11:11 PM
 *
 *  AgentVerse
 *  Integrates multiple AI models with a modular clean architecture.
 *
 *
 */

package com.atech.api_integration_common.tuning

import com.atech.api_integration_common.domain.model.AvModelConfig

/**
 * Defines an interface for tuning AI model configurations in the AgentVerse system.
 * Implementations of this interface can apply specific tuning strategies to modify the model configuration based on certain criteria or requirements, allowing for dynamic adjustments to the model's behavior during response generation.
 * @see AvModelConfig
 */
interface AvModelTuning {
    /**
     * Applies tuning to the given model configuration and returns the modified configuration.
     * @param modelConfig The original model configuration to be tuned.
     * @return The modified model configuration after applying the tuning strategy.
     */
    fun applyTuning(modelConfig: AvModelConfig): AvModelConfig
}