package com.duchastel.simon.solenne.fakes

import com.duchastel.simon.solenne.util.url.UrlOpener

class FakeUrlOpener : UrlOpener {
    var lastLaunchedUrl: String? = null
        private set

    override fun launchUrl(url: String) {
        lastLaunchedUrl = url
    }
}
