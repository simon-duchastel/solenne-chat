package com.duchastel.simon.solenne.network

import kotlinx.serialization.json.Json

/**
 * Used for serializing and deserializing JSON objects.
 */
val JsonParser = Json {
    isLenient = true
    ignoreUnknownKeys = true
}