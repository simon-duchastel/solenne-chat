package com.duchastel.simon.solenne.screens.conversationlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.screens.conversationlist.ConversationListScreen.Event
import com.duchastel.simon.solenne.ui.components.SolenneScaffold
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import solennechatapp.composeapp.generated.resources.Res
import solennechatapp.composeapp.generated.resources.screen_title_conversations
import solennechatapp.composeapp.generated.resources.conversation_item_text
import solennechatapp.composeapp.generated.resources.new_conversation_button
import solennechatapp.composeapp.generated.resources.settings_button

@Composable
fun ConversationListUi(state: ConversationListScreen.State, modifier: Modifier) {
    val eventSink = state.eventSink
    val conversations = state.conversations

    SolenneScaffold(
        title = stringResource(Res.string.screen_title_conversations),
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(conversations) { conversationId ->
                TextButton(
                    onClick = {
                        eventSink(Event.ConversationClicked(conversationId))
                    }
                ) {
                    Text(stringResource(Res.string.conversation_item_text, conversationId))
                }
            }
            item {
                TextButton(
                    onClick = {
                        eventSink(Event.NewConversationClicked)
                    }
                ) {
                    Text(stringResource(Res.string.new_conversation_button))
                }
            }
            item {
                Button(onClick = { eventSink(Event.SettingsClicked) }) {
                    Text(stringResource(Res.string.settings_button))
                }
            }
        }
    }
}

@Preview
@Composable
internal fun ConversationListUi_Preview() {
    ConversationListUi(
        modifier = Modifier,
        state = ConversationListScreen.State(
            persistentListOf("123", "456", "789"),
        )
    )
}
