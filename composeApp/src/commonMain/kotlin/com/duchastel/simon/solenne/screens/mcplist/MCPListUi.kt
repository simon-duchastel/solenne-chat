package com.duchastel.simon.solenne.screens.mcplist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.data.tools.McpServer
import com.duchastel.simon.solenne.screens.mcplist.MCPListScreen.Event
import com.duchastel.simon.solenne.ui.components.BackButton
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MCPListUi(state: MCPListScreen.State, modifier: Modifier) {
    val eventSink = state.eventSink
    val servers = state.mcpServers

    Column(modifier = modifier.fillMaxSize()) {
        Row {
            BackButton(
                modifier = Modifier.padding(8.dp),
                onClick = { eventSink(Event.BackPressed) },
            )
            Modifier.weight(1f)
            Text("MCP Servers")
            Modifier.weight(1f)
        }
        LazyColumn(
            modifier = Modifier.weight(1f).padding(8.dp)
        ) {
            items(servers) { server ->
                MCPServerItem(
                    server = server,
                    onConnectClick = { eventSink(Event.ConnectToServer(server)) },
                )
            }
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
        state = MCPListScreen.State(
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