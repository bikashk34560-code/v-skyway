package com.vskyway.network

// Request Models
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessageItem>,
    val temperature: Double = 0.7
)

data class ChatMessageItem(
    val role: String, // "system", "user", "assistant"
    val content: String
)

// Response Models
data class ChatResponse(
    val id: String,
    val choices: List<Choice>
)

data class Choice(
    val message: ChatMessageItem,
    val finish_reason: String
)