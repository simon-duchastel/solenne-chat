package com.duchastel.simon.solenne.util.url

import dev.zacsweers.metro.Inject
import java.awt.Desktop
import java.net.URI

class JvmUrlOpener @Inject constructor(
    private val desktop: Desktop
) : UrlOpener {
    override fun launchUrl(url: String) {
        val uri = URI(url)
        desktop.browse(uri)
    }
}