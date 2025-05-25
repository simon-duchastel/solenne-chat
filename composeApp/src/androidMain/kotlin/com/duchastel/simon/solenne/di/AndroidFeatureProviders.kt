package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.data.features.AndroidFeatures
import com.duchastel.simon.solenne.data.features.Features
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.SingleIn

interface AndroidFeatureProviders {
    @Binds
    @SingleIn(AppScope::class)
    fun AndroidFeatures.bind(): Features
}