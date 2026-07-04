package com.vskyway.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

// Table 1: AI Providers (Custom Models)
@Entity(tableName = "ai_providers")
data class AiProviderEntity(
    @PrimaryKey val id: String,
    val name: String,
    val baseUrl: String,
    val apiKeyEncrypted: String, // Keystore se encrypt hone ke baad yahan save hoga
    val isDefault: Boolean = false
)

// Table 2: Chat Sessions (Isolated Seasons)
@Entity(tableName = "chat_sessions")
data class SessionEntity(
    @PrimaryKey val sessionId: String, // UUID
    val title: String,
    val createdAt: Long,
    val lastActiveAt: Long,
    val preferredProviderId: String? // Har session apna model yaad rakhega
)

// Table 3: Chat Messages (Context retention)
@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE // Session delete toh message bhi delete
        )
    ],
    indices = [Index("sessionId")]
)
data class MessageEntity(
    @PrimaryKey val messageId: String,
    val sessionId: String,
    val role: String, // "user", "assistant", "system"
    val content: String,
    val timestamp: Long,
    val attachedFilePath: String? = null // Manual file upload link
)

// Table 4: Approval Engine Rules (Always Allow/Deny)
@Entity(tableName = "approval_rules")
data class ApprovalRuleEntity(
    @PrimaryKey(autoGenerate = true) val ruleId: Int = 0,
    val targetAppPackage: String,
    val actionType: String, // e.g., "CLICK_SEND", "OPEN_LINK"
    val isAllowed: Boolean
)