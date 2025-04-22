package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.data.ai.AiChatRepository
import com.duchastel.simon.solenne.data.ai.AiChatRepositoryImpl
import com.duchastel.simon.solenne.data.chat.ChatMessageRepository
import com.duchastel.simon.solenne.data.chat.ChatMessageRepositoryImpl
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.SingleIn

interface DataProviders {

    @SingleIn(AppScope::class)
    @Binds
    fun ChatMessageRepositoryImpl.bind(): ChatMessageRepository


    @SingleIn(AppScope::class)
    @Binds
    fun AiChatRepositoryImpl.bind(): AiChatRepository
}