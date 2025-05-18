package com.duchastel.simon.solenne.util.url

import android.content.Context
import android.content.Intent
import dev.zacsweers.metro.Inject
import androidx.core.net.toUri

class AndroidUrlOpener @Inject constructor(
    private val context: Context
) : UrlOpener {
    override fun launchUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}