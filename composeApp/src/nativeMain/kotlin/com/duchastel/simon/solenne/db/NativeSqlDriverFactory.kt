package com.duchastel.simon.solenne.db

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.duchastel.simon.solenne.ChatDatabase
import dev.zacsweers.metro.Inject

@Inject
class NativeSqlDriverFactory : SqlDriverFactory {
    override fun createChatSqlDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = ChatDatabase.Schema.synchronous(),
            name = SqlDriverFactory.CHAT_DB_NAME,
        )
    }

    override fun createMcpServerSqlDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = ChatDatabase.Schema.synchronous(),
            name = SqlDriverFactory.MCP_SERVER_DB_NAME,
        )
    }
}