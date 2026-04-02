package com.atech.agent.impl

import com.atech.agent.contract.Agent
import com.atech.agent.contract.AgentInput
import com.atech.agent.contract.AgentResult
import com.atech.core.orchestrator.AgentOrchestrator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatAgent @Inject constructor(
    private val orchestrator: AgentOrchestrator,
) : Agent {

    override val id: String = "chat"
    override val displayName: String = "Chat Agent"

    override suspend fun execute(input: AgentInput): Result<AgentResult> =
        orchestrator.sendUserPrompt(
            conversationId = input.conversationId,
            provider = input.provider,
            modelId = input.modelId,
            prompt = input.prompt,
            systemPrompt = "You are AgentVerse ChatAgent. Be concise, correct, and helpful.",
            modelConfig = input.modelConfig,
            memorySize = input.memorySize,
            onChunk = { chunk ->
                if (chunk.contentDelta.isNotBlank()) {
                    input.onStreamDelta?.invoke(chunk.contentDelta)
                }
            },
        ).map { AgentResult(it) }
}
