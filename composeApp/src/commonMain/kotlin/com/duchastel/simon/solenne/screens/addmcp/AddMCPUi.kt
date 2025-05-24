package com.duchastel.simon.solenne.screens.addmcp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen.Event
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen.ServerType
import com.duchastel.simon.solenne.ui.components.SolenneScaffold

@Composable
fun AddMCPUi(state: AddMCPScreen.State, modifier: Modifier = Modifier) {
    val eventSink = state.eventSink

    SolenneScaffold(
        modifier = modifier,
        title = "Add MCP Server",
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = state.serverName,
                onValueChange = { eventSink(Event.ServerNameChanged(it)) },
                label = { Text("Server Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = state.serverType == ServerType.REMOTE,
                    onClick = { eventSink(Event.ServerTypeChanged(ServerType.REMOTE)) }
                )
                Text("Remote Server")
                
                Spacer(modifier = Modifier.width(16.dp))
                
                RadioButton(
                    selected = state.serverType == ServerType.LOCAL,
                    onClick = { eventSink(Event.ServerTypeChanged(ServerType.LOCAL)) }
                )
                Text("Local Server")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show appropriate config based on server type
            when (state.serverType) {
                ServerType.REMOTE -> RemoteServerConfig(state, eventSink)
                ServerType.LOCAL -> LocalServerConfig(state, eventSink)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { 
                    val config = if (state.serverType == ServerType.REMOTE) {
                        state.remoteConfig
                    } else {
                        state.localConfig
                    }
                    state.saveEnabled?.onSavePressed?.invoke(state.serverName, config)
                },
                enabled = state.isSaveEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
private fun RemoteServerConfig(
    state: AddMCPScreen.State,
    eventSink: (AddMCPScreen.Event) -> Unit
) {
    OutlinedTextField(
        value = state.remoteConfig.url,
        onValueChange = { eventSink(Event.RemoteUrlChanged(it)) },
        label = { Text("Server URL") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun LocalServerConfig(
    state: AddMCPScreen.State,
    eventSink: (Event) -> Unit
) {
    Column {
        OutlinedTextField(
            value = state.localConfig.command,
            onValueChange = { eventSink(Event.CommandChanged(it)) },
            label = { Text("Command") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Environment Variables")
        
        state.localConfig.environmentVariables.forEach { (name, value) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(name, modifier = Modifier.weight(1f))
                Text(value, modifier = Modifier.weight(1f))
                IconButton(onClick = { eventSink(Event.RemoveEnvironmentVariable(name)) }) {
                    Icon(Icons.Default.Close, contentDescription = "Remove")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Add new environment variable
        var newVarName by remember { mutableStateOf("") }
        var newVarValue by remember { mutableStateOf("") }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = newVarName,
                onValueChange = { newVarName = it },
                label = { Text("Name") },
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            OutlinedTextField(
                value = newVarValue,
                onValueChange = { newVarValue = it },
                label = { Text("Value") },
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = { 
                    if (newVarName.isNotEmpty()) {
                        eventSink(Event.AddEnvironmentVariable(newVarName, newVarValue))
                        newVarName = ""
                        newVarValue = ""
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Config Preview
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Config Preview")
                Spacer(modifier = Modifier.height(8.dp))
                
                val envVars = state.localConfig.environmentVariables
                val jsonPreview = buildString {
                    appendLine("{")
                    appendLine("  \"command\": \"${state.localConfig.command.split("\"").joinToString("\\\"")}\"")
                    
                    if (envVars.isNotEmpty()) {
                        appendLine(",")
                        appendLine("  \"env\": {")
                        
                        envVars.entries.forEachIndexed { index, (key, value) ->
                            append("    \"$key\": \"$value\"")
                            if (index < envVars.size - 1) {
                                appendLine(",")
                            } else {
                                appendLine()
                            }
                        }
                        appendLine("  }")
                    }
                    
                    appendLine("}")
                }
                
                Text(jsonPreview)
            }
        }
    }
}
