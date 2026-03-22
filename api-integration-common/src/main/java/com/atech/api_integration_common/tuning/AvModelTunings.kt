/*
 *
 *  Copyright (c) 2026 Ayaan.
 *  Licensed under the MIT License.
 *
 *  Created: 2026
 *  Modified: 3/18/26, 11:15 PM
 *
 *  AgentVerse
 *  Integrates multiple AI models with a modular clean architecture.
 *
 *
 */

package com.atech.api_integration_common.tuning

import com.atech.api_integration_common.model.request.AvModelConfig

/**
 * Provides a collection of predefined model tuning configurations for the AgentVerse system,
 * allowing for easy application of specific tuning strategies to AI model configurations based on common use cases, such as creative writing, precise Q&A, code generation, conversational agents, structured data extraction, and long-form content creation. Each tuning configuration modifies the model parameters to optimize the output for the intended use case, ensuring that the AI models can be effectively utilized in various scenarios.
 * @see AvModelConfig
 * @see AvModelTuning
 */
object AvModelTunings {

    /**
     * Tuning configuration for creative tasks, such as storytelling, brainstorming, and imaginative outputs. This tuning increases the temperature and topP to encourage more diverse and creative responses, while also applying moderate frequency and presence penalties to reduce repetition and encourage new ideas.
     * This configuration is ideal for applications that require a high degree of creativity and originality in the generated content.
     * @see AvModelConfig
     */
    object Creative : AvModelTuning {
        override fun applyTuning(modelConfig: AvModelConfig) = modelConfig.copy(
            modelName = "creative",
            configDescription = "High creativity for storytelling, brainstorming, and imaginative outputs",
            modelVersion = "1.0",
            temperature = 1.2,
            topP = 0.95,
            frequencyPenalty = 0.5,
            presencePenalty = 0.4,
            maxTokens = 2048
        )
    }

    /**
     * Tuning configuration for precise tasks, such as factual Q&A, summarization, and classification. This tuning reduces the temperature and topP to encourage more focused and deterministic responses, while keeping frequency and presence penalties low to allow for accurate repetition of relevant information.
     * This configuration is ideal for applications that require high accuracy and reliability in the generated content,
     * such as when the model is used for extracting specific information, providing concise summaries, or answering factual questions where creativity is not desired.
     * @see AvModelConfig
     */
    object Precise : AvModelTuning {
        override fun applyTuning(modelConfig: AvModelConfig) = modelConfig.copy(
            modelName = "precise",
            configDescription = "Low temperature for factual Q&A, summarization, and classification",
            modelVersion = "1.0",
            temperature = 0.2,
            topP = 0.85,
            frequencyPenalty = 0.0,
            presencePenalty = 0.0,
            maxTokens = 1024
        )
    }

    /**
     * Tuning configuration for code generation and debugging tasks. This tuning significantly reduces the temperature to encourage near-deterministic output, while applying a moderate frequency penalty to discourage repetition of the same code snippets. The presence penalty is set to zero to allow for necessary repetition of relevant code constructs, and stop sequences are defined to signal the end of code blocks.
     * This configuration is ideal for applications that require precise and reliable code generation, such as when
     * the model is used for generating code snippets, providing debugging suggestions, or assisting with programming tasks where accuracy and consistency are crucial.
     * @see AvModelConfig
     * @see AvModelConfig.stopSequences
     */
    object Code : AvModelTuning {
        override fun applyTuning(modelConfig: AvModelConfig) = modelConfig.copy(
            modelName = "code",
            configDescription = "Near-deterministic output for code generation and debugging",
            modelVersion = "1.0",
            temperature = 0.1,
            topP = 0.9,
            frequencyPenalty = 0.2,
            presencePenalty = 0.0,
            maxTokens = 4096,
            stopSequences = listOf("```\n\n")
        )
    }

    /**
     * Tuning configuration for conversational agents and interactive assistants. This tuning balances creativity and coherence by setting a moderate temperature and topP, while applying frequency and presence penalties to encourage engaging and dynamic conversations without excessive repetition. The maxTokens is set to a moderate value to allow for meaningful interactions while preventing overly long responses.
     * This configuration is ideal for applications that require a natural and engaging conversational experience, such as
     * when the model is used for chatbots, virtual assistants, or any interactive application where maintaining a dynamic and coherent conversation is important.
     * @see AvModelConfig
     */
    object Conversational : AvModelTuning {
        override fun applyTuning(modelConfig: AvModelConfig) = modelConfig.copy(
            modelName = "conversational",
            configDescription = "Balanced settings for chat agents and interactive assistants",
            modelVersion = "1.0",
            temperature = 0.75,
            topP = 1.0,
            frequencyPenalty = 0.3,
            presencePenalty = 0.2,
            maxTokens = 512
        )
    }

    /**
     * Tuning configuration for structured data extraction tasks, such as generating JSON, XML, or other structured formats. This tuning sets the temperature to zero to encourage deterministic output, while keeping topP at 1.0 to allow for all relevant tokens to be considered. Frequency and presence penalties are set to zero to allow for necessary repetition of relevant tokens, and stop sequences are defined to signal the end of structured data blocks.
     * This configuration is ideal for applications that require reliable and consistent generation of structured data, such as when the model is used for extracting information in a specific format, generating structured responses, or any scenario where the output needs to adhere to a defined structure without creativity.
     * @see AvModelConfig
     * @see AvModelConfig.stopSequences
     */
    object Structured : AvModelTuning {
        override fun applyTuning(modelConfig: AvModelConfig) = modelConfig.copy(
            modelName = "structured",
            configDescription = "Greedy decoding for reliable JSON, XML, and structured data extraction",
            modelVersion = "1.0",
            temperature = 0.0,
            topP = 1.0,
            frequencyPenalty = 0.0,
            presencePenalty = 0.0,
            maxTokens = 2048,
            stopSequences = listOf("}\n\n", "]\n\n")
        )
    }

    /**
     * Tuning configuration for long-form content creation, such as articles, reports, and essays. This tuning sets a moderate temperature and topP to encourage creativity while maintaining coherence over longer outputs, while applying frequency and presence penalties to reduce repetition and encourage the introduction of new ideas throughout the content. The maxTokens is set to a high value to allow for extensive responses.
     * This configuration is ideal for applications that require the generation of long-form content, such as when the model is used for writing articles, generating reports, or any scenario where the output needs to be creative yet coherent over an extended length.
     * @see AvModelConfig
     */
    object LongForm : AvModelTuning {
        override fun applyTuning(modelConfig: AvModelConfig) = modelConfig.copy(
            modelName = "long-form",
            configDescription = "Moderate creativity with high repetition penalty for articles and reports",
            modelVersion = "1.0",
            temperature = 0.8,
            topP = 0.95,
            frequencyPenalty = 0.6,
            presencePenalty = 0.3,
            maxTokens = 8192
        )
    }

    /**
     * Composite tuning configuration that allows for the combination of multiple tuning strategies. This class takes a variable number of AvModelTuning instances and applies them sequentially to a given model configuration, allowing for flexible and customizable tuning based on specific requirements.
     * This configuration is ideal for applications that require a combination of different tuning strategies, such as when the model needs to be tuned for multiple use cases or when specific adjustments are needed that are not covered by the predefined tuning configurations.
     * @param tunings A variable number of AvModelTuning instances to be applied in sequence.
     * @see AvModelTuning
     */
    class Composite(private vararg val tunings: AvModelTuning) : AvModelTuning {
        override fun applyTuning(modelConfig: AvModelConfig): AvModelConfig =
            tunings.fold(modelConfig) { config, tuning -> tuning.applyTuning(config) }
    }
}