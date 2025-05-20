package com.duchastel.simon.solenne.di

import android.content.Context
import com.duchastel.simon.solenne.db.AndroidSqlDriverFactory
import com.duchastel.simon.solenne.db.DbSettings
import com.duchastel.simon.solenne.db.SqlDriverFactory
import com.duchastel.simon.solenne.db.aiapikey.AIApiKeySettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

interface AndroidDbProviders {
    @Provides
    @SingleIn(AppScope::class)
    fun provideSqlDriverProvider(context: Context): SqlDriverFactory {
        return AndroidSqlDriverFactory(context)
    }

    @Provides
    @AIApiKeySettings
    @SingleIn(AppScope::class)
    fun provideAIModelScopeSettings(context: Context): ObservableSettings {
        return SharedPreferencesSettings(
            context.getSharedPreferences(
                "com.duchastel.simon.solenne.di.AIApiKeySettings",
                Context.MODE_PRIVATE
            )
        )
    }

    @Provides
    @DbSettings
    @SingleIn(AppScope::class)
    fun provideDbSettings(context: Context): ObservableSettings {
        return SharedPreferencesSettings(
            context.getSharedPreferences(
                "com.duchastel.simon.solenne.di.DbSettings",
                Context.MODE_PRIVATE
            )
        )
    }
}