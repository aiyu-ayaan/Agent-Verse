package com.atech.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atech.data.local.entity.ProviderUsageAggregate
import com.atech.data.local.entity.TokenUsageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TokenUsageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TokenUsageEntity)

    @Query(
        """
        SELECT
            provider AS provider_name,
            SUM(prompt_tokens) AS total_prompt_tokens,
            SUM(completion_tokens) AS total_completion_tokens,
            SUM(total_tokens) AS total_tokens,
            COUNT(*) AS total_requests
        FROM token_usage
        GROUP BY provider
        ORDER BY total_tokens DESC
        """,
    )
    fun observeProviderUsage(): Flow<List<ProviderUsageAggregate>>
}