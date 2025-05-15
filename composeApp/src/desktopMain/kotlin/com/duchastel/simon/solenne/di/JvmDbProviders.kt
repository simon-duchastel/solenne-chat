package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.db.JvmSqlDriverFactory
import com.duchastel.simon.solenne.db.SqlDriverFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.SingleIn

interface JvmDbProviders {
    @SingleIn(AppScope::class)
    @Binds
    fun JvmSqlDriverFactory.bind(): SqlDriverFactory
}