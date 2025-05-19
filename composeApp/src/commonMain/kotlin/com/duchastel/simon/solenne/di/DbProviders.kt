package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.db.SqlDriverFactory
import com.duchastel.simon.solenne.db.aiapikey.AIApiKeyDb
import com.duchastel.simon.solenne.db.aiapikey.AIApiKeySettings
import com.duchastel.simon.solenne.db.aiapikey.AIApiKeySettingsImpl
import com.duchastel.simon.solenne.db.chat.ChatDatabase
import com.duchastel.simon.solenne.db.chat.ChatMessageDb
import com.duchastel.simon.solenne.db.chat.SQLDelightChatDb
import com.duchastel.simon.solenne.db.mcp.McpToolsDb
import com.duchastel.simon.solenne.db.mcp.SqlDelightMcpToolsDb
import com.duchastel.simon.solenne.db.mcpserver.McpServerDatabase
import com.russhwolf.settings.ObservableSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

interface DbProviders {

    @SingleIn(AppScope::class)
    @Provides
    fun provideChatDatabase(sqlDriverFactory: SqlDriverFactory): ChatDatabase {
        return ChatDatabase(sqlDriverFactory.createChatSqlDriver())
    }

    @SingleIn(AppScope::class)
    @Provides
    fun provideMcpServerDatabase(sqlDriverFactory: SqlDriverFactory): McpServerDatabase {
        return McpServerDatabase(sqlDriverFactory.createMcpServerSqlDriver())
    }

    @SingleIn(AppScope::class)
    @Provides
    fun provideChatMessageDb(chatDatabase: ChatDatabase): ChatMessageDb {
        return SQLDelightChatDb(chatDatabase)
    }

    @SingleIn(AppScope::class)
    @Provides
    fun provideMcpToolsDb(mcpServerDatabase: McpServerDatabase): McpToolsDb {
        return SqlDelightMcpToolsDb(mcpServerDatabase)
    }

    @SingleIn(AppScope::class)
    @Provides
    fun provideAIModelScopeDb(
        @AIApiKeySettings settings: ObservableSettings,
    ): AIApiKeyDb {
        return AIApiKeySettingsImpl(settings)
    }
}