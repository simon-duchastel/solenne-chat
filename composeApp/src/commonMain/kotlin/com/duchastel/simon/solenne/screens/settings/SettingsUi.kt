package com.duchastel.simon.solenne.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.screens.settings.SettingsScreen.Event
import com.duchastel.simon.solenne.screens.settings.SettingsScreen.State
import com.duchastel.simon.solenne.ui.components.BackButton
import com.duchastel.simon.solenne.ui.components.GithubSourceFooter
import com.duchastel.simon.solenne.ui.components.SettingsRow
import com.duchastel.simon.solenne.ui.components.SolenneScaffold
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
                SettingsRow(
                    Modifier.fillMaxWidth().padding(8.dp),
                    text = "Configure AI Models",
                    onClick = { eventSink(Event.ConfigureAIModelPressed) },
                )

                SettingsRow(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    text = "Configure MCP Servers",
                    onClick = { eventSink(Event.ConfigureMcpPressed) },
                )

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