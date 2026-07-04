package com.vskyway.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

// Custom Exceptions for Smart Handling
class RateLimitException(message: String) : IOException(message)
class PaymentRequiredException(message: String) : IOException(message)

class FailoverInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        when (response.code) {
            429 -> {
                Log.e("SkywayNetwork", "429 Hit: Rate Limit Reached. Triggering Failover...")
                response.close() // Close the failed response
                throw RateLimitException("AI provider is rate limited.")
            }
            402 -> {
                Log.e("SkywayNetwork", "402 Hit: Out of Credits. Triggering Failover...")
                response.close()
                throw PaymentRequiredException("AI provider requires payment/credits.")
            }
        }
        return response
    }
}