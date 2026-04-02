package com.atech.agent.contract

interface Agent {
    val id: String
    val displayName: String

    suspend fun execute(input: AgentInput): Result<AgentResult>
}