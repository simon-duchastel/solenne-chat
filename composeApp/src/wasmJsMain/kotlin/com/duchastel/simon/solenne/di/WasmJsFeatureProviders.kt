package com.duchastel.simon.solenne.di

import com.duchastel.simon.solenne.data.features.Features
import com.duchastel.simon.solenne.data.features.WasmJsFeatures
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.SingleIn

interface WasmJsFeatureProviders {
    @Binds
    @SingleIn(AppScope::class)
    fun WasmJsFeatures.bind(): Features
}