package com.duchastel.simon.solenne.di

import app.cash.sqldelight.db.SqlDriver
import com.duchastel.simon.solenne.db.chat.ChatMessageDb
import com.duchastel.simon.solenne.db.chat.InMemoryChatDb
import com.duchastel.simon.solenne.db.chat.SQLDelightChatDb
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.SingleIn

interface DbProviders {

    @SingleIn(AppScope::class)
    @Binds
    fun InMemoryChatDb.bind(): ChatMessageDb

//    @SingleIn(AppScope::class)
//    @Binds
//    fun SQLDelightChatDb.bind(driver: SqlDriver): ChatMessageDb
}