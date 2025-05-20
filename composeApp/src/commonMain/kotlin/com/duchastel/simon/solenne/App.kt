package com.duchastel.simon.solenne

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.duchastel.simon.solenne.screens.splash.SplashScreen
import com.duchastel.simon.solenne.util.circuit.LocalNavigator
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
            val backStack = rememberSaveableBackStack(root = SplashScreen)
            val navigator = rememberCircuitNavigator(backStack, onRootPop = {})

            CompositionLocalProvider(LocalNavigator provides navigator) {
                // NavigableCircuitContent appears not to pop the backstack
                // unless we delegate to the navigator from the BackHandler
                BackHandler {
                    navigator.pop()
                }
                NavigableCircuitContent(navigator, backStack)
            }
        }
    }
}