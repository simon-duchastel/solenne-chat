package com.duchastel.simon.solenne.screens.topbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.screens.topbar.TopBarScreen.Event
import com.duchastel.simon.solenne.screens.topbar.TopBarScreen.State
import com.duchastel.simon.solenne.ui.components.BackButton
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TopBarUi(
    state: State,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (state.showBackButton) {
            BackButton(
                onClick = { state.eventSink(Event.BackPressed) },
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        Spacer(Modifier.weight(1f))
        Text(state.title)
        Spacer(Modifier.weight(1f))

        if (state.showBackButton) {
            // add extra space at the end to balance out the back button
            Spacer(modifier = Modifier.width(48.dp + 8.dp + 16.dp))
        }
    }
}

@Preview
@Composable
internal fun TopBarUi_ShowBackButton_Preview() {
    TopBarUi(
        state = State(
            title = "Top Bar",
            showBackButton = true,
        )
    )
}

@Preview
@Composable
internal fun TopBarUi_NoBackButton_Preview() {
    TopBarUi(
        state = State(
            title = "Top Bar",
            showBackButton = false,
        )
    )
}