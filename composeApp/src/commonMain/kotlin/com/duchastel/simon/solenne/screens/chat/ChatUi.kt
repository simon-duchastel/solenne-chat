package com.duchastel.simon.solenne.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.fakes.chatMessages
import com.slack.circuit.runtime.ui.Ui
import com.duchastel.simon.solenne.screens.chat.ChatScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatUi(state: ChatScreen.State, modifier: Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            reverseLayout = true,
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Render newest messages at the bottom
            items(chatMessages.asReversed()) { message ->
                ChatMessage(message)
            }
        }
        MessageInput(
            onSend = { messageText ->
                // Callback stub, should link to eventSink in presenter
            },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        )
    }
}

data class ChatMessageModel(val text: String, val isUser: Boolean)

@Composable
private fun ChatMessage(message: ChatMessageModel, modifier: Modifier = Modifier) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (message.isUser) Color(0xFFE1FFC7) else Color(0xFFF1F0F0)
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = bubbleColor,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(12.dp)
        ) {
            Text(text = message.text)
        }
    }
}

@Composable
private fun MessageInput(
    onSend: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var input by remember { mutableStateOf("") }
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message...") }
        )
        Spacer(Modifier.width(8.dp))
        Button(
            onClick = {
                if (input.isNotBlank()) {
                    onSend(input)
                    input = ""
                }
            },
            enabled = input.isNotBlank()
        ) {
            Text("Send")
        }
    }
}

@Preview
@Composable
internal fun ChatUi_Preview() {
    ChatUi(
        modifier = Modifier,
        state = ChatScreen.State()
    )
}