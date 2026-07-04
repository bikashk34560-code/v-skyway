package com.vskyway.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SkywayDao {

    // --- Sessions ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    @Query("SELECT * FROM chat_sessions ORDER BY lastActiveAt DESC")
    fun getAllSessionsLive(): Flow<List<SessionEntity>>

    @Query("DELETE FROM chat_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: String)

    // --- Messages ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    suspend fun getMessagesForSession(sessionId: String): List<MessageEntity>

    // --- AI Providers ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProvider(provider: AiProviderEntity)

    @Query("SELECT * FROM ai_providers")
    suspend fun getAllProviders(): List<AiProviderEntity>

    // --- Approval Rules ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApprovalRule(rule: ApprovalRuleEntity)

    @Query("SELECT * FROM approval_rules WHERE targetAppPackage = :packageName AND actionType = :action")
    suspend fun checkRule(packageName: String, action: String): ApprovalRuleEntity?
}