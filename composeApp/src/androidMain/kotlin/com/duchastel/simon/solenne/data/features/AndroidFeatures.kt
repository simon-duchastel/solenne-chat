package com.duchastel.simon.solenne.data.features

import dev.zacsweers.metro.Inject

@Inject
class AndroidFeatures : Features {
    override val localMcpServerAvailable: Boolean = false
}