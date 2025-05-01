package com.duchastel.simon.solenne.di

import com.russhwolf.settings.ObservableSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

@DependencyGraph(AppScope::class)
interface DesktopApplicationGraph : ApplicationGraph {

    @Provides
    fun provideObservableSettings(): ObservableSettings {
        return SharedPreferencesSettings(sharedPreferences)
    }
}