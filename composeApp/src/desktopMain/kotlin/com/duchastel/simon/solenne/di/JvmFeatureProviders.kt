package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.data.features.Features
import com.duchastel.simon.solenne.data.features.JvmFeatures
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.SingleIn

interface JvmFeatureProviders {
    @Binds
    @SingleIn(AppScope::class)
    fun JvmFeatures.bind(): Features
}