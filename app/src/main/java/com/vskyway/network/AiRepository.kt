package com.vskyway.network

import com.vskyway.data.db.SkywayDao
import com.vskyway.security.SecurityHelper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// Retrofit API Interface
interface AiApiService {
    @POST("v1/chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") token: String,
        @Body request: ChatRequest
    ): ChatResponse
}

class AiRepository(private val dao: SkywayDao) {

    // Dynamic Network Client Builder
    private fun getApiClient(baseUrl: String): AiApiService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(FailoverInterceptor()) // Injecting our smart interceptor
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AiApiService::class.java)
    }

    /**
     * Yeh function chat bhejega aur failover handle karega.
     */
    suspend fun sendMessageWithFailover(
        sessionId: String,
        userPrompt: String,
        primaryProviderId: String,
        fallbackProviderId: String? = null
    ): String {
        try {
            return attemptApiCall(sessionId, userPrompt, primaryProviderId)
        } catch (e: RateLimitException) {
            // THE FAILOVER LOGIC: Agar pehla AI fail hua, aur fallback available hai
            if (fallbackProviderId != null) {
                return "[SYSTEM] Switching to Backup Model due to Rate Limit...\n" + 
                       attemptApiCall(sessionId, userPrompt, fallbackProviderId)
            } else {
                return "[ERROR] Rate Limit Reached. No backup provider configured."
            }
        } catch (e: Exception) {
            return "[ERROR] Network Failure: ${e.message}"
        }
    }

    private suspend fun attemptApiCall(sessionId: String, userPrompt: String, providerId: String): String {
        // 1. Fetch Provider Config from DB (Decrypted safely)
        // Note: In real app, you'd call dao.getProvider(providerId)
        val baseUrl = "https://openrouter.ai/api/" // Example
        val apiKey = SecurityHelper.decryptData("encrypted_key_from_db")

        // 2. Fetch past context from DB to maintain memory
        val pastMessages = dao.getMessagesForSession(sessionId)
        
        // 3. Convert DB messages to Network format
        val networkMessages = pastMessages.map { 
            ChatMessageItem(role = it.role, content = it.content) 
        }.toMutableList()

        // 4. Add the new user prompt
        networkMessages.add(ChatMessageItem(role = "user", content = userPrompt))

        val request = ChatRequest(
            model = "auto", // Provider depends on UI selection
            messages = networkMessages
        )

        val apiService = getApiClient(baseUrl)
        val response = apiService.getChatCompletion("Bearer $apiKey", request)

        return response.choices.firstOrNull()?.message?.content ?: "No response from AI."
    }
}