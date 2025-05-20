package com.duchastel.simon.solenne.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.screens.settings.SettingsScreen.Event
import com.duchastel.simon.solenne.screens.settings.SettingsScreen.State
import com.duchastel.simon.solenne.ui.components.BuyMeCoffeeFooter
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

    SolenneScaffold(
        title = "Settings",
        modifier = modifier,
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
            SettingsRow(
                Modifier.fillMaxWidth(),
                text = "Configure AI Models",
                onClick = { eventSink(Event.ConfigureAIModelPressed) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsRow(
                modifier = Modifier.fillMaxWidth(),
                text = "Configure MCP Servers",
                onClick = { eventSink(Event.ConfigureMcpPressed) },
            )

            Spacer(modifier = Modifier.weight(1f))

            BuyMeCoffeeFooter(
                onClick = { eventSink(Event.BuyMeACoffeePressed) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            GithubSourceFooter(
                onClick = { eventSink(Event.ViewSourcePressed) },
                modifier = Modifier.fillMaxWidth()
            )
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