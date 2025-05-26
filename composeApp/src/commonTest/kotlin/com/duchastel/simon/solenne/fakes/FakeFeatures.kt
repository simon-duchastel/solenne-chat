package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.data.features.Features

internal class FakeFeatures(
    override val localMcpServerAvailable: Boolean = true
) : Features