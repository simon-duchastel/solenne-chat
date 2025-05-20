package com.duchastel.simon.solenne.db

import app.cash.sqldelight.db.SqlDriver

interface SqlDriverFactory {
    fun createSqlDriver(): SqlDriver

    companion object {
        const val DB_NAME = "com.duchastel.simon.solenne.db"
    }
}