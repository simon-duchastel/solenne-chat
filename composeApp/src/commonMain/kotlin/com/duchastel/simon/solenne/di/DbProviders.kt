package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.db.chat.ChatMessageDb
import com.duchastel.simon.solenne.db.chat.InMemoryChatDb
import dev.zacsweers.metro.Binds

interface DbProviders {
    @Binds
    fun InMemoryChatDb.bind(): ChatMessageDb
}