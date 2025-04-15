package com.duchastel.simon.solenne

import androidx.compose.ui.window.ComposeUIViewController
import com.duchastel.simon.solenne.di.IosApplicationGraph

fun makeUiViewController(graph: IosApplicationGraph) = ComposeUIViewController {
    App(graph.circuit)
}
