package com.atech.core.di

import com.atech.core.orchestrator.AgentOrchestrator
import com.atech.core.orchestrator.AgentOrchestratorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {
    @Binds
    abstract fun bindAgentOrchestrator(impl: AgentOrchestratorImpl): AgentOrchestrator
}