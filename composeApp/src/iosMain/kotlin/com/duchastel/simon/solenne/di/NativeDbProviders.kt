package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.db.DbSettings
import com.duchastel.simon.solenne.db.NativeSqlDriverFactory
import com.duchastel.simon.solenne.db.SqlDriverFactory
import com.duchastel.simon.solenne.db.aiapikey.AIApiKeySettings
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

interface NativeDbProviders {

    @SingleIn(AppScope::class)
    @Binds
    fun NativeSqlDriverFactory.bind(): SqlDriverFactory

    @Provides
    @AIApiKeySettings
    @SingleIn(AppScope::class)
    fun provideAIModelScopeSettings(): ObservableSettings {
        return NSUserDefaultsSettings.Factory()
            .create(name = "com.duchastel.simon.solenne.di.AIApiKeySettings")
    }

    @Provides
    @DbSettings
    @SingleIn(AppScope::class)
    fun provideDbSettings(): ObservableSettings {
        return NSUserDefaultsSettings.Factory()
            .create(name = "com.duchastel.simon.solenne.di.DbSettings")
    }
}