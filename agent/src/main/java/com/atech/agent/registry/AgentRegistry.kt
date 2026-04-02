package com.atech.agent.registry

import com.atech.agent.contract.Agent
import com.atech.agent.impl.ChatAgent
import com.atech.agent.impl.CodeAgent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgentRegistry @Inject constructor(
    chatAgent: ChatAgent,
    codeAgent: CodeAgent,
) {
    private val agents: Map<String, Agent> = listOf(chatAgent, codeAgent).associateBy { it.id }

    fun get(agentId: String): Agent =
        agents[agentId] ?: error("Unknown agent id: $agentId")

    fun list(): List<Agent> = agents.values.sortedBy { it.id }
}