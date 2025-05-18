package com.duchastel.simon.solenne.util.url

import dev.zacsweers.metro.Inject
import kotlinx.browser.window

class WasmJsUrlOpener @Inject constructor() : UrlOpener {
    override fun launchUrl(url: String) {
        window.open(url)
    }
}