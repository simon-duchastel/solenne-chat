package com.duchastel.simon.solenne.db

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.duchastel.simon.solenne.db.chat.ChatDatabase
import com.duchastel.simon.solenne.db.mcpserver.McpServerDatabase
import dev.zacsweers.metro.Inject

@Inject
class AndroidSqlDriverFactory(private val context: Context) : SqlDriverFactory {
    override fun createChatSqlDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = ChatDatabase.Schema.synchronous(),
            context = context,
            name = SqlDriverFactory.CHAT_DB_NAME,
        )
    }

    override fun createMcpServerSqlDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = McpServerDatabase.Schema.synchronous(),
            context = context,
            name = SqlDriverFactory.MCP_SERVER_DB_NAME,
        )
    }
}