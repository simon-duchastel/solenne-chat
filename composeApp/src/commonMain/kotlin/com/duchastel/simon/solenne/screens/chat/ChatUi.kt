package com.duchastel.simon.solenne.screens.chat

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.runtime.ui.Ui
import com.duchastel.simon.solenne.screens.chat.ChatScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatUi(state: ChatScreen.State, modifier: Modifier) {
    Text(text = "Work in progress", modifier = modifier)
}

// Previews in commonMain aren't supported yet in Android Support but will be imminently:
// https://youtrack.jetbrains.com/issue/KTIJ-32720/Support-common-org.jetbrains.compose.ui.tooling.preview.Preview-in-IDEA-and-Android-Studio
@Preview
@Composable
fun ChatUi_Preview() {
    ChatUi(
        modifier = Modifier,
        state = ChatScreen.State()
    )
}