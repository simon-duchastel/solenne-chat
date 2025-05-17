package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.db.JvmSqlDriverFactory
import com.duchastel.simon.solenne.db.SqlDriverFactory
import com.duchastel.simon.solenne.db.aiapikey.AIApiKeySettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

interface JvmDbProviders {
    @SingleIn(AppScope::class)
    @Binds
    fun JvmSqlDriverFactory.bind(): SqlDriverFactory

    @Provides
    @AIApiKeySettings
    @SingleIn(AppScope::class)
    fun provideAIModelScopeSettings(): ObservableSettings {
        return PreferencesSettings.Factory()
            .create("com.duchastel.simon.solenne.di.ObservableSettings")
    }
}