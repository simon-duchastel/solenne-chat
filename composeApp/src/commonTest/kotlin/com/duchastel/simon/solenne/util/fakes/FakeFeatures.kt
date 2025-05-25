package com.duchastel.simon.solenne.util.fakes

import com.duchastel.simon.solenne.data.features.Features

internal class FakeFeatures(
    override val localMcpServerAvailable: Boolean = true
) : Features