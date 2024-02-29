package com.enigmastation.kgpt

import com.enigmastation.kgpt.model.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.Headers.Companion.toHeaders
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.net.URI
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class GPT(
    val apiToken: String? = null,
    val endpoint: URI = URI("https://api.openai.com/v1/chat/completions"),
    private val connectTimeoutTime: Duration = 3.seconds,
    private val readTimeoutTime: Duration = 12.seconds
) {
    private val endpointURL = endpoint.toURL()
    private val callTimeoutTime = connectTimeoutTime.plus(readTimeoutTime)

    private val client =
        OkHttpClient()
            .newBuilder()
            .callTimeout(callTimeoutTime.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .readTimeout(readTimeoutTime.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .connectTimeout(connectTimeoutTime.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .addInterceptor { chain ->
                val request = chain.request()
                val requestWithUserAgent =
                    request
                        .newBuilder()
                        .removeHeader("User-Agent")
                        .addHeader(
                            "User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36"
                        )
                        .build()
                chain.proceed(requestWithUserAgent)
            }
            .addInterceptor(HttpLoggingInterceptor())
            .build()

    fun query(prompt: String): BaseGPTResponse {
        if (apiToken == null) {
            return NullGPTResponse()
        }
        val dataPacket = GPTMessageContainer(listOf(GPTMessage(prompt)))
        return query(dataPacket)

    }

    private fun query(messageContainer: GPTMessageContainer): BaseGPTResponse {
        val url = endpointURL.toHttpUrlOrNull() ?: return NullGPTResponse()
        apiToken ?: return NullGPTResponse()
        val builder = url.newBuilder()
        val requestBuilder = Request.Builder()
            .url(builder.build())
            .headers(
                mapOf(
                    "Authorization" to "Bearer $apiToken"
                ).toHeaders()
            )
            .post(
                mapper.writeValueAsString(messageContainer)
                    .toRequestBody("application/json; charset=utf-8".toMediaType())
            )
        val request = requestBuilder.build()
        client
            .newCall(request)
            .execute()
            .use { response ->
                return when {
                    response.isSuccessful -> mapper.readValue(
                        response.body?.string(),
                        GPTResponse::class.java
                    )
                    else ->
                        throw Exception("${response.code} ${response.message}")
                }
            }
    }

    companion object {
        val mapper = buildMapper()

        private fun buildMapper(): ObjectMapper {
            val mapper = ObjectMapper()
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            mapper.registerModules(
                KotlinModule.Builder()
                    .withReflectionCacheSize(100)
                    .configure(KotlinFeature.NullToEmptyCollection, true)
                    .configure(KotlinFeature.NullToEmptyMap, true)
                    .configure(KotlinFeature.NullIsSameAsDefault, true)
                    .configure(KotlinFeature.SingletonSupport, true)
                    .configure(KotlinFeature.StrictNullChecks, false)
                    .build()
            )
            return mapper
        }
    }
}