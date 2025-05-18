package com.duchastel.simon.solenne.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(AppScope::class)
interface JvmApplicationGraph : ApplicationGraph,
    JvmDbProviders,
    JvmUtilProviders
