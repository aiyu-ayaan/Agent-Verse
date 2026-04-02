package com.atech.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.atech.data.local.dao.ConversationDao
import com.atech.data.local.dao.ConversationSessionDao
import com.atech.data.local.dao.TokenUsageDao
import com.atech.data.local.entity.ConversationMessageEntity
import com.atech.data.local.entity.ConversationSessionEntity
import com.atech.data.local.entity.TokenUsageEntity

@Database(
    entities = [ConversationMessageEntity::class, TokenUsageEntity::class, ConversationSessionEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AgentVerseDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun tokenUsageDao(): TokenUsageDao
    abstract fun conversationSessionDao(): ConversationSessionDao
}
