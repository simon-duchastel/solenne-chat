package com.duchastel.simon.solenne.db

import app.cash.sqldelight.db.SqlDriver

interface SqlDriverFactory {
    fun createChatSqlDriver(): SqlDriver
    fun createMcpServerSqlDriver(): SqlDriver

    companion object {
        const val CHAT_DB_NAME = "com.duchastel.simon.solenne.db.chat"
        const val MCP_SERVER_DB_NAME = "com.duchastel.simon.solenne.db.mcpserver"
    }
}