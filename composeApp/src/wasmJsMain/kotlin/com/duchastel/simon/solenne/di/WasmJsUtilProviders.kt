package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.util.url.WasmJsUrlOpener
import com.duchastel.simon.solenne.util.url.UrlOpener
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.SingleIn

interface WasmJsUtilProviders {
    @SingleIn(AppScope::class)
    @Binds
    fun WasmJsUrlOpener.bind(): UrlOpener
}