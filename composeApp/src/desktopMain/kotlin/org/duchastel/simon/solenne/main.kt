package org.duchastel.simon.solenne

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SolenneChatApp",
    ) {
        App()
    }
}