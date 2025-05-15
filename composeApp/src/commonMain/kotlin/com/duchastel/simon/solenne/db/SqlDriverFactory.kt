package com.duchastel.simon.solenne.db

import app.cash.sqldelight.db.SqlDriver

interface SqlDriverFactory {
    fun createDriver(): SqlDriver
}