package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.Database
import com.duchastel.simon.solenne.db.DatabaseWrapper
import com.duchastel.simon.solenne.db.DatabaseWrapperImpl
import com.duchastel.simon.solenne.db.DbSettings
import com.duchastel.simon.solenne.db.SqlDriverFactory
import com.duchastel.simon.solenne.db.aiapikey.AIApiKeyDb
import com.duchastel.simon.solenne.db.aiapikey.AIApiKeySettings
import com.duchastel.simon.solenne.db.aiapikey.AIApiKeySettingsImpl
import com.duchastel.simon.solenne.db.chat.ChatMessageDb
import com.duchastel.simon.solenne.db.chat.SQLDelightChatDb
import com.duchastel.simon.solenne.db.mcp.McpToolsDb
import com.duchastel.simon.solenne.db.mcp.SqlDelightMcpToolsDb
import com.russhwolf.settings.ObservableSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

interface DbProviders {

    @SingleIn(AppScope::class)
    @Provides
    fun DatabaseWrapperImpl.bind(): DatabaseWrapper

    @SingleIn(AppScope::class)
    @Provides
    fun provideChatMessageDb(databaseWrapper: DatabaseWrapper): ChatMessageDb {
        return SQLDelightChatDb(databaseWrapper)
    }

    @SingleIn(AppScope::class)
    @Provides
    fun provideMcpToolsDb(databaseWrapper: DatabaseWrapper): McpToolsDb {
        return SqlDelightMcpToolsDb(databaseWrapper)
    }

    @SingleIn(AppScope::class)
    @Provides
    fun provideAIModelScopeDb(
        @AIApiKeySettings settings: ObservableSettings,
    ): AIApiKeyDb {
        return AIApiKeySettingsImpl(settings)
    }
}