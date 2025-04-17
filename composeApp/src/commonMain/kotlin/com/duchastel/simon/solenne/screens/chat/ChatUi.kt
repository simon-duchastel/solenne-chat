package com.duchastel.simon.solenne.screens.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.data.ChatMessage
import com.duchastel.simon.solenne.fakes.ChatMessagesFake
import com.duchastel.simon.solenne.ui.components.ChatMessage
import com.duchastel.simon.solenne.ui.components.MessageInput
import com.duchastel.simon.solenne.ui.model.toUIChatMessage
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatUi(state: ChatScreen.State, modifier: Modifier) {
    val messages = state.messages
    var input by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            reverseLayout = true,
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages.asReversed()) { message ->
                ChatMessage(message)
            }
        }
        MessageInput(
            input = input,
            onInputChange = { newInput -> input = newInput },
            onSend = { },
            sendEnabled = input.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
    }
}

@Preview
@Composable
internal fun ChatUi_Preview() {
    ChatUi(
        modifier = Modifier,
        state = ChatScreen.State(
            messages = ChatMessagesFake.chatMessages
                .map(ChatMessage::toUIChatMessage)
                .toPersistentList()
        )
    )
}