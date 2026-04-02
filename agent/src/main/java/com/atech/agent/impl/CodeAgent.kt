package com.atech.agent.impl

import com.atech.agent.contract.Agent
import com.atech.agent.contract.AgentInput
import com.atech.agent.contract.AgentResult
import com.atech.core.orchestrator.AgentOrchestrator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CodeAgent @Inject constructor(
    private val orchestrator: AgentOrchestrator,
) : Agent {

    override val id: String = "code"
    override val displayName: String = "Code Agent"

    override suspend fun execute(input: AgentInput): Result<AgentResult> =
        orchestrator.sendUserPrompt(
            conversationId = input.conversationId,
            provider = input.provider,
            modelId = input.modelId,
            prompt = input.prompt,
            systemPrompt = "You are AgentVerse CodeAgent. Write production-safe code and explain assumptions.",
            modelConfig = input.modelConfig,
            memorySize = input.memorySize,
        ).map { AgentResult(it) }
}