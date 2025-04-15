package org.duchastel.simon.solenne.screens.chat

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.slack.circuit.runtime.ui.Ui
import org.jetbrains.compose.ui.tooling.preview.Preview

class ChatUi : Ui<ChatScreen.State> {
    @Composable
    override fun Content(state: ChatScreen.State, modifier: Modifier) {
        Text(text = "Work in progress", modifier = modifier)
    }
}

@Preview
@Composable
fun ChatUi_Preview() {
    ChatUi().Content(
        modifier = Modifier,
        state = ChatScreen.State()
    )
}