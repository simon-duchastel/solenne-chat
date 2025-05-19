package com.duchastel.simon.solenne.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.duchastel.simon.solenne.ChatDatabase
import com.duchastel.simon.solenne.McpServerDatabase
import dev.zacsweers.metro.Inject
import java.io.File

@Inject
class JvmSqlDriverFactory : SqlDriverFactory {
    override fun createChatSqlDriver(): SqlDriver {
        val databasePath = File(System.getProperty("user.home"), SqlDriverFactory.CHAT_DB_NAME)
        return JdbcSqliteDriver(url = "jdbc:sqlite:${databasePath.absolutePath}").apply {
            ChatDatabase.Schema.create(this)
        }
    }

    override fun createMcpServerSqlDriver(): SqlDriver {
        val databasePath = File(System.getProperty("user.home"), SqlDriverFactory.MCP_SERVER_DB_NAME)
        return JdbcSqliteDriver(url = "jdbc:sqlite:${databasePath.absolutePath}").apply {
            McpServerDatabase.Schema.create(this)
        }
    }
}