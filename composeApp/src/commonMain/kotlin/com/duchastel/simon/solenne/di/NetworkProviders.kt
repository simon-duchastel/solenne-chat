package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.data.ai.AIModelScope.GeminiModelScope
import com.duchastel.simon.solenne.network.ai.AiChatApi
import com.duchastel.simon.solenne.network.ai.gemini.GEMINI
import com.duchastel.simon.solenne.network.ai.gemini.GeminiApi
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.sse.SSE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface NetworkProviders {
    @Provides
    fun provideHttpClient(): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
        install(SSE) {
            this.maxReconnectionAttempts = 3
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
    }

    @Binds
    fun GeminiApi.bind(): AiChatApi<GeminiModelScope>
}