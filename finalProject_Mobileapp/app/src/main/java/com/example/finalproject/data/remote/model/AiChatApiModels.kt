package com.example.finalproject.data.remote.model

data class AiChatRequest(
    val message: String
)

data class AiChatResponse(
    val reply: String,
    val sessionId: Long?
)

data class AiChatMessageItem(
    val id: Long,
    val role: String,       // "USER" hoặc "ASSISTANT"
    val content: String,
    val createdAt: String?
)
