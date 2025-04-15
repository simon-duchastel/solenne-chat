package com.duchastel.simon.solenne.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.createGraph

@DependencyGraph(AppScope::class)
@SingleIn(AppScope::class)
interface IosApplicationGraph : ApplicationGraph {
    companion object {
        fun create(): IosApplicationGraph = createGraph()
    }
}
