package com.duchastel.simon.solenne.screens.addmcp

import androidx.compose.foundation.clickable
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
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen.ServerConfig
import com.duchastel.simon.solenne.screens.addmcp.AddMCPScreen.ServerType
import com.duchastel.simon.solenne.ui.components.SolenneScaffold
import org.jetbrains.compose.ui.tooling.preview.Preview

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
                Row(
                    modifier = Modifier.clickable(
                        onClick = { eventSink(Event.ServerTypeChanged(ServerType.REMOTE)) }
                    )
                ) {
                    RadioButton(
                        selected = state.config is ServerConfig.Remote,
                        onClick = null,
                    )
                    Text("Remote Server")
                }
                
                Spacer(modifier = Modifier.width(16.dp))

                Row(
                    modifier = Modifier.clickable(
                        enabled = state.localMcpEnabled,
                        onClick = { eventSink(Event.ServerTypeChanged(ServerType.LOCAL)) }
                    )
                ) {
                    RadioButton(
                        selected = state.config is ServerConfig.Local,
                        onClick = null,
                    )
                    Text("Local Server")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (state.config) {
                is ServerConfig.Remote -> RemoteServerConfig(
                    state.config,
                    onUrlChanged = state.config.onUrlChanged,
                )
                is ServerConfig.Local -> LocalServerConfig(
                    state.config,
                    onCommandChanged = state.config.onCommandChanged,
                    onEnvironmentVariableAdded = { key, value ->
                        state.config.onEnvironmentVariableUpdated(key, value)
                    },
                    onEnvironmentVariableRemoved = { key ->
                        state.config.onEnvironmentVariableUpdated(key, null)
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    state.saveEnabled?.onSavePressed?.invoke(state.serverName, state.config)
                },
                enabled = state.saveEnabled != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
private fun RemoteServerConfig(
    config: ServerConfig.Remote,
    onUrlChanged: (String) -> Unit,
) {
    OutlinedTextField(
        value = config.url,
        onValueChange = onUrlChanged,
        label = { Text("Server URL") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun LocalServerConfig(
    config: ServerConfig.Local,
    onCommandChanged: (String) -> Unit,
    onEnvironmentVariableAdded: (String, String) -> Unit,
    onEnvironmentVariableRemoved: (String) -> Unit,
) {
    Column {
        OutlinedTextField(
            value = config.command,
            onValueChange = onCommandChanged,
            label = { Text("Command") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Environment Variables")
        
        config.environmentVariables.forEach { (name, value) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(name, modifier = Modifier.weight(1f))
                Text(value, modifier = Modifier.weight(1f))
                IconButton(onClick = { onEnvironmentVariableRemoved(name) }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove environment variable"
                    )
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
                        onEnvironmentVariableAdded(newVarName, newVarValue)
                        newVarName = ""
                        newVarValue = ""
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add environment variable")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        // Config Preview
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Config Preview")
                Spacer(modifier = Modifier.height(8.dp))
                
                val envVars = config.environmentVariables
                val jsonPreview = buildString {
                    appendLine("{")
                    appendLine("  \"command\": \"${config.command.split("\"").joinToString("\\\"")}\"")
                    
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

@Preview
@Composable
internal fun AddMCPUi_RemotePreview() {
    AddMCPUi(
        state = AddMCPScreen.State(
            serverName = "Remote Production Server",
            config = ServerConfig.Remote(
                url = "https://example.com/api",
                onUrlChanged = {},
            ),
            localMcpEnabled = true,
            saveEnabled = AddMCPScreen.SaveEnabled(
                onSavePressed = { _, _ -> }
            )
        )
    )
}

@Preview
@Composable
internal fun AddMCPUi_LocalPreview() {
    AddMCPUi(
        state = AddMCPScreen.State(
            serverName = "Local Development Server",
            config = ServerConfig.Local(
                command = "python server.py",
                environmentVariables = mapOf(
                    "PORT" to "8080",
                    "DEBUG" to "true"
                ),
                onCommandChanged = {},
                onEnvironmentVariableUpdated = { _, _ -> }
            ),
            localMcpEnabled = true,
            saveEnabled = AddMCPScreen.SaveEnabled(
                onSavePressed = { _, _ -> }
            )
        )
    )
}

@Preview
@Composable
internal fun RemoteServerConfig_Preview() {
    RemoteServerConfig(
        config = ServerConfig.Remote(
            url = "https://example.com/api",
            onUrlChanged = {},
        ),
        onUrlChanged = {},
    )
}

@Preview
@Composable
internal fun LocalServerConfig_Preview() {
    LocalServerConfig(
        config = ServerConfig.Local(
            command = "python server.py",
            environmentVariables = mapOf(
                "PORT" to "8080",
                "DEBUG" to "true",
                "API_KEY" to "abc123"
            ),
            onCommandChanged = {},
            onEnvironmentVariableUpdated = { _, _ -> }
        ),
        onCommandChanged = {},
        onEnvironmentVariableAdded = { _, _ -> },
        onEnvironmentVariableRemoved = { }
    )
}
