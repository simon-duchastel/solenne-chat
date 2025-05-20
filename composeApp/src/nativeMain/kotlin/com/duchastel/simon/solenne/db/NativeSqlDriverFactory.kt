package com.duchastel.simon.solenne.db

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.duchastel.simon.solenne.Database
import dev.zacsweers.metro.Inject

@Inject
class NativeSqlDriverFactory : SqlDriverFactory {
    override fun createSqlDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = Database.Schema.synchronous(),
            name = SqlDriverFactory.DB_NAME,
        )
    }
}