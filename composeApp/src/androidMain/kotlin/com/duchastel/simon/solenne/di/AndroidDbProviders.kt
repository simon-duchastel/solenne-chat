package com.duchastel.simon.solenne.di

import android.content.Context
import com.duchastel.simon.solenne.db.AndroidSqlDriverFactory
import com.duchastel.simon.solenne.db.SqlDriverFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

interface AndroidDbProviders {
    @Provides
    @SingleIn(AppScope::class)
    fun provideSqlDriverProvider(context: Context): SqlDriverFactory {
        return AndroidSqlDriverFactory(context)
    }
}