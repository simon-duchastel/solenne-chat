package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.util.url.AndroidUrlOpener
import com.duchastel.simon.solenne.util.url.UrlOpener
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.SingleIn

interface AndroidUtilProviders {
        @SingleIn(AppScope::class)
        @Binds
        fun AndroidUrlOpener.bind(): UrlOpener
}