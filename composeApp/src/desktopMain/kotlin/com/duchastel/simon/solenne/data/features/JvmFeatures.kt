package com.duchastel.simon.solenne.data.features

import dev.zacsweers.metro.Inject

@Inject
class JvmFeatures : Features {
    override val localMcpServerAvailable: Boolean = true
}