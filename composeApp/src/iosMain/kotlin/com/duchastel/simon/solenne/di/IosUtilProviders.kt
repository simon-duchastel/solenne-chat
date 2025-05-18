package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.util.url.IosUrlOpener
import com.duchastel.simon.solenne.util.url.UrlOpener
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.SingleIn

interface IosUtilProviders {
    @SingleIn(AppScope::class)
    @Binds
    fun IosUrlOpener.bind(): UrlOpener
}