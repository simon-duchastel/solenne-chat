package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.util.url.JvmUrlOpener
import com.duchastel.simon.solenne.util.url.UrlOpener
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import java.awt.Desktop

interface JvmUtilProviders {
    @SingleIn(AppScope::class)
    @Binds
    fun JvmUrlOpener.bind(): UrlOpener

    @Provides
    fun provideDesktop(): Desktop {
        return Desktop.getDesktop()
    }
}