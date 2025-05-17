package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.db.SqlDriverFactory
import com.duchastel.simon.solenne.db.WasmJsSqlDriverFactory
import com.duchastel.simon.solenne.db.aiapikey.AIApiKeySettings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.observable.makeObservable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

interface WasmJsDbProviders {

    @SingleIn(AppScope::class)
    @Binds
    fun WasmJsSqlDriverFactory.bind(): SqlDriverFactory

    @OptIn(ExperimentalSettingsApi::class)
    @Provides
    @AIApiKeySettings
    @SingleIn(AppScope::class)
    fun provideAIModelScopeSettings(): ObservableSettings {
        return StorageSettings().makeObservable()
    }
}