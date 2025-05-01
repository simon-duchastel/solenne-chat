package com.duchastel.simon.solenne

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.duchastel.simon.solenne.screens.modelselector.ModelProviderSelectorScreen
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.internal.BackHandler
import com.slack.circuit.foundation.rememberCircuitNavigator
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(circuit: Circuit) {
    MaterialTheme {
        CircuitCompositionLocals(circuit) {
            val backStack = rememberSaveableBackStack(root = ModelProviderSelectorScreen)
            val navigator = rememberCircuitNavigator(backStack, onRootPop = {})

            // NavigableCircuitContent appears not to pop the backstack
            // unless we delegate to the navigator from the BackHandler
            BackHandler {
                navigator.pop()
            }
            NavigableCircuitContent(navigator, backStack)
        }
    }
}