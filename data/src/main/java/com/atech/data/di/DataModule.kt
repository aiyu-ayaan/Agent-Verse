package com.atech.data.di

import android.content.Context
import androidx.room.Room
import com.atech.api_integration_common.contract.AvCredentialProvider
import com.atech.core.repository.ConversationRepository
import com.atech.core.repository.ProviderConfigRepository
import com.atech.core.repository.TokenUsageRepository
import com.atech.data.local.dao.ConversationDao
import com.atech.data.local.dao.TokenUsageDao
import com.atech.data.local.db.AgentVerseDatabase
import com.atech.data.repository.RoomConversationRepository
import com.atech.data.repository.RoomTokenUsageRepository
import com.atech.data.settings.SharedPrefsProviderConfigRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AgentVerseDatabase =
        Room.databaseBuilder(
            context,
            AgentVerseDatabase::class.java,
            "agentverse.db",
        ).build()

    @Provides
    fun provideConversationDao(database: AgentVerseDatabase): ConversationDao = database.conversationDao()

    @Provides
    fun provideTokenUsageDao(database: AgentVerseDatabase): TokenUsageDao = database.tokenUsageDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindings {

    @Binds
    @Singleton
    abstract fun bindConversationRepository(impl: RoomConversationRepository): ConversationRepository

    @Binds
    @Singleton
    abstract fun bindTokenUsageRepository(impl: RoomTokenUsageRepository): TokenUsageRepository

    @Binds
    @Singleton
    abstract fun bindProviderConfigRepository(
        impl: SharedPrefsProviderConfigRepository,
    ): ProviderConfigRepository

    @Binds
    @Singleton
    abstract fun bindCredentialProvider(impl: SharedPrefsProviderConfigRepository): AvCredentialProvider
}