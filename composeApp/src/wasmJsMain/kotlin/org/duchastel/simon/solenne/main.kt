package org.duchastel.simon.solenne

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import dev.zacsweers.metro.createGraph
import org.duchastel.simon.solenne.di.ApplicationGraph

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val applicationGraph = createGraph<ApplicationGraph>()
    ComposeViewport(document.body!!) {
        App(applicationGraph.circuit)
    }
}