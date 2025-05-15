package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.db.NativeSqlDriverFactory
import com.duchastel.simon.solenne.db.SqlDriverFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.SingleIn

interface NativeDbProviders {

    @SingleIn(AppScope::class)
    @Binds
    fun NativeSqlDriverFactory.bind(): SqlDriverFactory
}