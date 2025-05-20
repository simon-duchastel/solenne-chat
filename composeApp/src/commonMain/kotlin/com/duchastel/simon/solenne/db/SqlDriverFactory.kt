package com.duchastel.simon.solenne.db

import app.cash.sqldelight.db.SqlDriver

interface SqlDriverFactory {
    fun createSqlDriver(): SqlDriver

    companion object {
        const val DB_NAME = "com.duchastel.simon.solenne.db"
        const val DB_VERSION_CURRENT = 2L
        const val DB_VERSION_PERSISTENCE_KEY = "com.duchastel.simon.solenne.db.version"
    }
}