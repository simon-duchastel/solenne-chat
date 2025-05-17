package com.duchastel.simon.solenne.di

import android.content.Context
import com.duchastel.simon.solenne.db.AndroidSqlDriverFactory
import com.duchastel.simon.solenne.db.SqlDriverFactory
import com.duchastel.simon.solenne.db.aimodelscope.AIModelScopeSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Named
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

interface AndroidDbProviders {
    @Provides
    @SingleIn(AppScope::class)
    fun provideSqlDriverProvider(context: Context): SqlDriverFactory {
        return AndroidSqlDriverFactory(context)
    }

    @Provides
    @AIModelScopeSettings
    @SingleIn(AppScope::class)
    fun provideAIModelScopeSettings(context: Context): ObservableSettings {
        return SharedPreferencesSettings(
            context.getSharedPreferences(
                "com.duchastel.simon.solenne.di.ObservableSettings",
                Context.MODE_PRIVATE
            )
        )
    }
}