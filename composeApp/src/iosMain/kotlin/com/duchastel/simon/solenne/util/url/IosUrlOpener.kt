package com.duchastel.simon.solenne.util.url

import dev.zacsweers.metro.Inject
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class IosUrlOpener @Inject constructor() : UrlOpener {
    override fun launchUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        UIApplication.sharedApplication.openURL(nsUrl)
    }
}