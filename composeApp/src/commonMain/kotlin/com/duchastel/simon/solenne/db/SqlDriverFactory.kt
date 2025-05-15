package com.duchastel.simon.solenne.db

import app.cash.sqldelight.db.SqlDriver

interface SqlDriverFactory {
    /**
     * Creates and returns a SqlDriver instance.
     * Platform implementations should handle initialization such as schema creation.
     */
    suspend fun createDriver(): SqlDriver
}