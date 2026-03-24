package com.example.finalproject.data.remote.api

import com.example.finalproject.data.remote.model.AiChatMessageItem
import com.example.finalproject.data.remote.model.AiChatRequest
import com.example.finalproject.data.remote.model.AiChatResponse
import com.example.finalproject.data.remote.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface AiTutorApiService {
    @POST("ai/chat")
    suspend fun sendMessage(@Body request: AiChatRequest): ApiResponse<AiChatResponse>

    @GET("ai/chat/history")
    suspend fun getHistory(): ApiResponse<List<AiChatMessageItem>>

    @DELETE("ai/chat/history")
    suspend fun clearHistory(): ApiResponse<Void?>
}
