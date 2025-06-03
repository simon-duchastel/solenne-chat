package com.duchastel.simon.solenne.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.data.chat.models.MessageAuthor
import com.duchastel.simon.solenne.fakes.ChatMessagesFake
import com.duchastel.simon.solenne.ui.model.UIChatMessage
import com.duchastel.simon.solenne.ui.model.toUIChatMessage
import com.mikepenz.markdown.m3.Markdown
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import solennechatapp.composeapp.generated.resources.Res
import solennechatapp.composeapp.generated.resources.using_tool_message
import solennechatapp.composeapp.generated.resources.tool_result_message
import solennechatapp.composeapp.generated.resources.collapse
import solennechatapp.composeapp.generated.resources.expand

@Composable
fun ChatMessage(
    message: UIChatMessage,
    modifier: Modifier = Modifier,
) {
    when (message) {
        is UIChatMessage.TextMessage -> {
            TextMessage(
                message = message,
                modifier = modifier
            )
        }

        is UIChatMessage.ToolMessage -> {
            ToolMessage(
                message = message,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun TextMessage(
    message: UIChatMessage.TextMessage,
    modifier: Modifier = Modifier,
) {
    val bubbleColor = if (message.isUser) Color(0xFFE1FFC7) else Color(0xFFF1F0F0)
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .background(
                    color = bubbleColor,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(12.dp)
        ) {
            Markdown(
                content = message.text,
                modifier = Modifier,
            )
        }
    }
}

@Composable
private fun ToolMessage(
    message: UIChatMessage.ToolMessage,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val bubbleColor = Color(0xFFF1F0F0) // Tool messages are always from AI

    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .background(
                    color = bubbleColor,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (message.result == null) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = stringResource(Res.string.using_tool_message, message.toolName),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = if (message.result == null) 8.dp else 0.dp)
                        )
                    }

                    if (message.result != null) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) stringResource(Res.string.collapse) else stringResource(Res.string.expand),
                            modifier = Modifier
                                .clickable { isExpanded = !isExpanded }
                                .size(20.dp)
                        )
                    }
                }

                if (isExpanded && message.result != null) {
                    Markdown(
                        content = message.result,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
internal fun UserChatMessage_Preview() {
    ChatMessage(
        message = ChatMessagesFake.chatMessages.first { it.author is MessageAuthor.User }.toUIChatMessage(),
    )
}

@Preview
@Composable
internal fun LLMChatMessage_Preview() {
    ChatMessage(
        message = ChatMessagesFake.chatMessages.first { it.author is MessageAuthor.AI }.toUIChatMessage(),
    )
}
