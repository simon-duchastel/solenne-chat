package com.duchastel.simon.solenne.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.screens.settings.SettingsScreen.Event
import com.duchastel.simon.solenne.screens.settings.SettingsScreen.State
import com.duchastel.simon.solenne.ui.components.BackButton
import com.duchastel.simon.solenne.ui.components.SolenneScaffold
import com.duchastel.simon.solenne.ui.components.GithubSourceFooter
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SettingsUi(
    state: State,
    modifier: Modifier = Modifier,
) {
    val eventSink = state.eventSink

    SolenneScaffold(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BackButton(
                    onClick = { eventSink(Event.BackPressed) },
                    modifier = Modifier.padding(end = 16.dp)
                )
                Text("Settings")
            }

            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp)
            ) {
                // Model Provider Selector option
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    elevation = 2.dp,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Configure Model Provider",
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { eventSink(Event.ModelProviderSelectorPressed) }) {
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = "Go to Model Provider"
                            )
                        }
                    }
                }

                // Add MCP option
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    elevation = 2.dp,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Add Model Provider Server",
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { eventSink(Event.AddMCPPressed) }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Go to Add MCP")
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                GithubSourceFooter(
                    onClick = { eventSink(Event.ViewSourcePressed) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
internal fun SettingsUi_Preview() {
    SettingsUi(
        state = State()
    )
}