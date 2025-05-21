package com.duchastel.simon.solenne.db

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.duchastel.simon.solenne.Database
import dev.zacsweers.metro.Inject

@Inject
class AndroidSqlDriverFactory(private val context: Context) : SqlDriverFactory {
    override suspend fun createSqlDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = Database.Schema.synchronous(),
            context = context,
            name = SqlDriverFactory.DB_NAME,
        )
    }
}