package com.duchastel.simon.solenne.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.duchastel.simon.solenne.Database
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.Properties

@Inject
class JvmSqlDriverFactory : SqlDriverFactory {
    override fun createSqlDriver(): SqlDriver {
        val databasePath = File(System.getProperty("user.home"), SqlDriverFactory.DB_NAME)
        val databaseAlreadyExists = databasePath.exists()
        return JdbcSqliteDriver(
            url = "jdbc:sqlite:${databasePath.absolutePath}",
            properties = Properties(),
        ).apply {
            runBlocking {
                if (!databaseAlreadyExists) {
                    Database.Schema.create(this@apply).await()
                }
            }
        }
    }
}