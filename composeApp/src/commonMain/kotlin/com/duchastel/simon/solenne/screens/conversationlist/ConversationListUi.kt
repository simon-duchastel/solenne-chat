package com.duchastel.simon.solenne.screens.conversationlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.screens.conversationlist.ConversationListScreen.Event
import com.duchastel.simon.solenne.ui.components.SolenneScaffold
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ConversationListUi(state: ConversationListScreen.State, modifier: Modifier) {
    val eventSink = state.eventSink
    val conversations = state.conversations

    SolenneScaffold(modifier = modifier) {
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
                    Text("Conversation $conversationId")
                }
            }
            item {
                TextButton(
                    onClick = {
                        eventSink(Event.NewConversationClicked)
                    }
                ) {
                    Text("New Conversation")
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