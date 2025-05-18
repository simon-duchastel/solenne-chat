package com.duchastel.simon.solenne.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.createGraph

@DependencyGraph(AppScope::class)
interface IosApplicationGraph : ApplicationGraph,
    NativeDbProviders,
    IosUtilProviders {

    companion object {
        fun create(): IosApplicationGraph = createGraph()
    }
}
