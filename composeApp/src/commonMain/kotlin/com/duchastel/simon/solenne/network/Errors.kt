package com.duchastel.simon.solenne.network

import com.duchastel.simon.solenne.util.Failure
import com.duchastel.simon.solenne.util.SolenneResult
import com.duchastel.simon.solenne.util.Success
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.SSEClientException
import io.modelcontextprotocol.kotlin.sdk.McpError
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/**
 * Convenience function to wrap a network call made with a Ktor [HttpClient].
 * Automatically catches and wraps any exceptions thrown by the MCP library such
 * as [IOException] and [McpError].
 */
suspend fun <T> wrapHttpCall(block: suspend () -> T): SolenneResult<T> {
    return try {
        val successResult = block()
        Success(successResult)
    } catch (ex: IOException) {
        // thrown by OkHttp when an error occurs communicating over the network
        Failure(ex)
    } catch (ex: SSEClientException) {
        // thrown by OkHttp when an error occurs in the SSE HTTP stream
        Failure(ex)
    } catch (ex: SerializationException) {
        // thrown by kotlinx serialization when an error occurs serializing or deserializing
        // a network model
        Failure(ex)
    }
}