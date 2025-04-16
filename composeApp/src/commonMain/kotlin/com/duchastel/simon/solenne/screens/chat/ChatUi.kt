package com.duchastel.simon.solenne.screens.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.ui.components.ChatMessage
import com.duchastel.simon.solenne.ui.components.MessageInput
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatUi(state: ChatScreen.State, modifier: Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            reverseLayout = true,
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(state.messages.asReversed()) { message ->
                ChatMessage(message)
            }
        }
        MessageInput(
            onSend = { messageText ->

            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
    }
}

@Preview
@Composable
internal fun ChatUi_Preview() {
    ChatUi(
        modifier = Modifier,
        state = ChatScreen.State(emptyList())
    )
}