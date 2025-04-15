package com.duchastel.simon.solenne

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import dev.zacsweers.metro.createGraph
import com.duchastel.simon.solenne.di.ApplicationGraph
import com.duchastel.simon.solenne.App

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val applicationGraph = createGraph<ApplicationGraph>()
    ComposeViewport(document.body!!) {
        App(applicationGraph.circuit)
    }
}