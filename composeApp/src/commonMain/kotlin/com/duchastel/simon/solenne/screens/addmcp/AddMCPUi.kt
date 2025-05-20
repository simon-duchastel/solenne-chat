package com.duchastel.simon.solenne.screens.addmcp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen.Event
import com.duchastel.simon.solenne.ui.components.BackButton
import com.duchastel.simon.solenne.ui.components.SolenneScaffold
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AddMCPUi(state: AddMCPScreen.State, modifier: Modifier) {
    val eventSink = state.eventSink

    SolenneScaffold(
        modifier = modifier,
        title = "Add MCP Server",
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = state.serverName,
                onValueChange = { eventSink(Event.ServerNameChanged(it)) },
                label = { Text("Server Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.serverUrl,
                onValueChange = { eventSink(Event.ServerUrlChanged(it)) },
                label = { Text("Server URL") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { eventSink(Event.SavePressed) },
                enabled = state.isSaveEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}

@Preview
@Composable
internal fun AddMCPUi_Preview() {
    AddMCPUi(
        modifier = Modifier,
        state = AddMCPScreen.State(
            serverName = "Test Server",
            serverUrl = "http://localhost:3000",
            isSaveEnabled = true
        )
    )
}