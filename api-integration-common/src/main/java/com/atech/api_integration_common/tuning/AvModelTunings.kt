package com.atech.api_integration_common.tuning

import com.atech.api_integration_common.model.AvModelConfig

object AvModelTunings {

    object Creative : AvModelTuning {
        override fun applyTuning(modelConfig: AvModelConfig): AvModelConfig =
            modelConfig.copy(
                temperature = 1.2,
                topP = 0.95,
                frequencyPenalty = 0.5,
                presencePenalty = 0.4,
                maxTokens = 2048
            )
    }

    object Precise : AvModelTuning {
        override fun applyTuning(modelConfig: AvModelConfig): AvModelConfig =
            modelConfig.copy(
                temperature = 0.2,
                topP = 0.85,
                frequencyPenalty = 0.0,
                presencePenalty = 0.0,
                maxTokens = 1024
            )
    }

    object Code : AvModelTuning {
        override fun applyTuning(modelConfig: AvModelConfig): AvModelConfig =
            modelConfig.copy(
                temperature = 0.1,
                topP = 0.9,
                frequencyPenalty = 0.2,
                presencePenalty = 0.0,
                maxTokens = 4096,
                stopSequences = listOf("```\n\n")
            )
    }

    object Conversational : AvModelTuning {
        override fun applyTuning(modelConfig: AvModelConfig): AvModelConfig =
            modelConfig.copy(
                temperature = 0.75,
                topP = 1.0,
                frequencyPenalty = 0.3,
                presencePenalty = 0.2,
                maxTokens = 1024
            )
    }

    object Structured : AvModelTuning {
        override fun applyTuning(modelConfig: AvModelConfig): AvModelConfig =
            modelConfig.copy(
                temperature = 0.0,
                topP = 1.0,
                frequencyPenalty = 0.0,
                presencePenalty = 0.0,
                maxTokens = 2048
            )
    }

    object LongForm : AvModelTuning {
        override fun applyTuning(modelConfig: AvModelConfig): AvModelConfig =
            modelConfig.copy(
                temperature = 0.8,
                topP = 0.95,
                frequencyPenalty = 0.6,
                presencePenalty = 0.3,
                maxTokens = 8192
            )
    }

    class Composite(private vararg val tunings: AvModelTuning) : AvModelTuning {
        override fun applyTuning(modelConfig: AvModelConfig): AvModelConfig =
            tunings.fold(modelConfig) { config, tuning -> tuning.applyTuning(config) }
    }
}