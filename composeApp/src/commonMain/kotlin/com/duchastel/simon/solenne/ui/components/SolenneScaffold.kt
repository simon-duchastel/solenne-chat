package com.duchastel.simon.solenne.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.duchastel.simon.solenne.screens.topbar.TopBarScreen
import com.slack.circuit.foundation.CircuitContent

/**
 * A scaffold component that provides basic functionality for all screens, including edge-to-edge
 * support by properly handling system insets.
 */
@Composable
fun SolenneScaffold(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = { CircuitContent(TopBarScreen(title = title)) }
    ) {
        content()
    }
}