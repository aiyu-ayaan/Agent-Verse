package com.atech.agent.di

import com.atech.agent.contract.Agent
import com.atech.agent.impl.ChatAgent
import com.atech.agent.impl.CodeAgent
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@Module
@InstallIn(SingletonComponent::class)
abstract class AgentBindings {
    @Binds
    @IntoMap
    @StringKey("chat")
    abstract fun bindChatAgent(impl: ChatAgent): Agent

    @Binds
    @IntoMap
    @StringKey("code")
    abstract fun bindCodeAgent(impl: CodeAgent): Agent
}