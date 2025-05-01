package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.db.chat.ChatMessageDb
import com.duchastel.simon.solenne.db.chat.LocalStorageDb
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.SingleIn

interface DbProviders {

    @SingleIn(AppScope::class)
    @Binds
    fun LocalStorageDb.bind(): ChatMessageDb
}