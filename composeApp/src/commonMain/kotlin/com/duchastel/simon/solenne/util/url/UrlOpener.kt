package com.duchastel.simon.solenne.util.url

/**
 * Interface for opening URLs on different platforms
 */
interface UrlOpener {
    /**
     * Opens the provided URL in the platform's default browser
     */
    fun launchUrl(url: String)
}