package com.duchastel.simon.solenne.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.duchastel.simon.solenne.Database
import dev.zacsweers.metro.Inject

@Inject
class AndroidSqlDriverFactory(private val context: Context) : SqlDriverFactory {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = Database.Schema,
            context = context,
            name = "solenne.db"
        )
    }
}