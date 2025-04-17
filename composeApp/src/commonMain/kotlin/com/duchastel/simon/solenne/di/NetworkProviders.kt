package com.duchastel.simon.solenne.di

import dev.zacsweers.metro.Provides
import io.ktor.client.HttpClient

interface NetworkProviders {
    @Provides
    fun provideHttpClient(): HttpClient = HttpClient()
}