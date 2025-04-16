package com.duchastel.simon.solenne.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MessageInput(
    input: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    sendEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = input,
            onValueChange = onInputChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Type a message...") }
        )
        Spacer(Modifier.width(8.dp))
        Button(
            onClick = {
                onSend()
            },
            enabled = sendEnabled
        ) {
            Text("Send")
        }
    }
}

@Preview
@Composable
fun MessageInputEnabled_Preview() {
    MessageInput(
        input = "Hello!",
        onInputChange = {},
        onSend = {},
        sendEnabled = true,
    )
}

@Preview
@Composable
fun wMessageInputDisabled_Preview() {
    MessageInput(
        input = "",
        onInputChange = {},
        onSend = {},
        sendEnabled = false,
    )
}