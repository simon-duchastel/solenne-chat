package com.duchastel.simon.solenne

import com.duchastel.simon.solenne.di.ApplicationGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.createGraph

@DependencyGraph(AppScope::class)
interface AndroidApplicationGraph : ApplicationGraph {
    val mcpRepository: McpRepository

    companion object {
        fun create(): AndroidApplicationGraph = createGraph()
    }
}
