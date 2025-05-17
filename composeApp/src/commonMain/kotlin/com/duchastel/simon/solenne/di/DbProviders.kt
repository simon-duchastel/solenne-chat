package com.duchastel.simon.solenne.di

import app.cash.sqldelight.db.SqlDriver
import com.duchastel.simon.solenne.Database
import com.duchastel.simon.solenne.db.SqlDriverFactory
import com.duchastel.simon.solenne.db.aimodelscope.AIApiKeyDb
import com.duchastel.simon.solenne.db.aimodelscope.AIApiKeyDbImpl
import com.duchastel.simon.solenne.db.aimodelscope.AIModelScopeSettings
import com.duchastel.simon.solenne.db.chat.ChatMessageDb
import com.duchastel.simon.solenne.db.chat.SQLDelightChatDb
import com.russhwolf.settings.ObservableSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

interface DbProviders {

    @SingleIn(AppScope::class)
    @Provides
    fun provideSqlDriver(sqlDriverFactory: SqlDriverFactory): SqlDriver {
        return sqlDriverFactory.createDriver()
    }

    @SingleIn(AppScope::class)
    @Provides
    fun provideDatabase(sqlDriver: SqlDriver): Database {
        return Database(sqlDriver)
    }

    @SingleIn(AppScope::class)
    @Provides
    fun provideChatMessageDb(database: Database): ChatMessageDb {
        return SQLDelightChatDb(database)
    }

    @SingleIn(AppScope::class)
    @Provides
    fun provideAIModelScopeDb(
        @AIModelScopeSettings settings: ObservableSettings,
    ): AIApiKeyDb {
        return AIApiKeyDbImpl(settings)
    }
}