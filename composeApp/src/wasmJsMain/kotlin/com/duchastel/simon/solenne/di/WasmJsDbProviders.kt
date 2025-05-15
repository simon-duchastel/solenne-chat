package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.db.SqlDriverFactory
import com.duchastel.simon.solenne.db.WasmJsSqlDriverFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.SingleIn

interface WasmJsDbProviders {

    @SingleIn(AppScope::class)
    @Binds
    fun WasmJsSqlDriverFactory.bind(): SqlDriverFactory
}