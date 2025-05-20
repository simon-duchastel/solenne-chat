package com.duchastel.simon.solenne.screens.mcplist

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.screens.mcplist.MCPListScreen.Event
import com.duchastel.simon.solenne.screens.mcplist.MCPListScreen.State
import com.duchastel.simon.solenne.ui.components.SolenneScaffold
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MCPListUi(state: State, modifier: Modifier) {
    val eventSink = state.eventSink
    val servers = state.mcpServers

    SolenneScaffold(
        title = "MCP Servers",
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(8.dp)
        ) {
            items(servers) { server ->
                MCPServerItem(
                    server = server,
                    onConnectClick = { eventSink(Event.ConnectToServer(server)) },
                )
            }
        }
        FloatingActionButton(
            onClick = { eventSink(Event.AddServerPressed) },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Server")
        }
    }
}

@Composable
private fun MCPServerItem(
    server: UIMCPServer,
    onConnectClick: () -> Unit,
) {
    Row {
        Text("Server: ${server.name} (${server.status})")

        if (server.status == UIMCPServer.Status.Disconnected) {
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = onConnectClick) {
                Text("Connect")
            }
        }
    }
}

@Preview
@Composable
internal fun MCPListUi_Preview() {
    MCPListUi(
        modifier = Modifier,
        state = State(
            mcpServers = persistentListOf(
                UIMCPServer(
                    "1",
                    "Server 1",
                    UIMCPServer.Status.Connected,
                ),UIMCPServer(
                    "2",
                    "Server 2",
                    UIMCPServer.Status.Disconnected,
                ),
            ),
        )
    )
}