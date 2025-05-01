package com.duchastel.simon.solenne

import android.content.Context
import com.duchastel.simon.solenne.di.ApplicationGraph
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraph

@DependencyGraph(AppScope::class)
interface AndroidApplicationGraph : ApplicationGraph {
    val applicationContext: Context

    @Provides
    fun provideObservableSettings(): ObservableSettings {
        val sharedPreferences = applicationContext.getSharedPreferences("solenne", 0)
        return SharedPreferencesSettings(sharedPreferences)
    }

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides applicationContext: Context): AndroidApplicationGraph
    }
}
