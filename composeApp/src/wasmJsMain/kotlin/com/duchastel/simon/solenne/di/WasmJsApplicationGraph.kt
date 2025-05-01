package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.db.chat.ChatMessageDb
import com.duchastel.simon.solenne.db.chat.InMemoryChatDb
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.createGraph

@DependencyGraph(AppScope::class)
interface WasmJsApplicationGraph : ApplicationGraph {

    companion object {
        fun create(): WasmJsApplicationGraph = createGraph()
    }
}