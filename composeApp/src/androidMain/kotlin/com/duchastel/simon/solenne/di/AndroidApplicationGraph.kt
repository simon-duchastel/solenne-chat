package com.duchastel.simon.solenne.di

import android.content.Context
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(AppScope::class)
interface AndroidApplicationGraph :
    ApplicationGraph,
    AndroidDbProviders,
    AndroidUtilProviders,
    AndroidFeatureProviders {

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides appContext: Context): AndroidApplicationGraph
    }
}
