package com.duchastel.simon.solenne

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.CircuitContent
import com.duchastel.simon.solenne.screens.chat.ChatScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(circuit: Circuit) {
    MaterialTheme {
        CircuitCompositionLocals(circuit) {
            CircuitContent(ChatScreen(conversationId = "sample-convo-1"))
        }
    }
}