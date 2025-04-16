package com.duchastel.simon.solenne.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.screens.chat.ChatScreen

@Composable
fun ChatMessage(
    message: ChatScreen.ChatMessage,
    modifier: Modifier = Modifier,
) {
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
