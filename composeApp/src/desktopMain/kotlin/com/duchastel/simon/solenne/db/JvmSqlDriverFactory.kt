package com.duchastel.simon.solenne.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.duchastel.simon.solenne.Database
import dev.zacsweers.metro.Inject
import java.io.File

@Inject
class JvmSqlDriverFactory : SqlDriverFactory {
    override fun createDriver(): SqlDriver {
        val databasePath = File(System.getProperty("user.home"), "solenne.db")
        return JdbcSqliteDriver(url = "jdbc:sqlite:${databasePath.absolutePath}").apply {
            Database.Schema.create(this)
        }
    }
}