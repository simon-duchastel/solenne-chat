package com.duchastel.simon.solenne.screens.modelproviderselector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duchastel.simon.solenne.screens.modelproviderselector.ModelProviderSelectorScreen.Event
import com.duchastel.simon.solenne.screens.modelproviderselector.UiModelProvider.DeepSeek
import com.duchastel.simon.solenne.screens.modelproviderselector.UiModelProvider.Gemini
import com.duchastel.simon.solenne.screens.modelproviderselector.UiModelProvider.OpenAI
import com.duchastel.simon.solenne.screens.modelproviderselector.UiModelProvider.Other
import com.duchastel.simon.solenne.ui.components.BackButton
import com.duchastel.simon.solenne.ui.components.ModelProviderButton
import com.duchastel.simon.solenne.ui.components.SolenneScaffold
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ModelProviderSelectorUi(
    state: ModelProviderSelectorScreen.State,
    modifier: Modifier,
) {
    val eventSink = state.eventSink

    SolenneScaffold(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackButton(
                    onClick = { eventSink(Event.BackPressed) },
                    modifier = Modifier.padding(end = 16.dp)
                )
                Text("Select AI Model")
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                items(state.models) { modelInfo ->
                    ModelProviderButton(
                        model = modelInfo,
                        onModelSelected = { eventSink(Event.ModelSelected(modelInfo)) }
                    )
                    Divider()
                }
            }
        }
    }
}

@Preview
@Composable
internal fun ModelSelectorUi_Preview() {
    val models = persistentListOf(
        Gemini,
        OpenAI,
        DeepSeek,
        Other("Custom provider")
    )

    ModelProviderSelectorUi(
        modifier = Modifier,
        state = ModelProviderSelectorScreen.State(
            models = models,
        )
    )
}