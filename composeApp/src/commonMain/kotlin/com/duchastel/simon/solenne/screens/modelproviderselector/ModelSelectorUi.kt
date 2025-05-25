package com.duchastel.simon.solenne.screens.modelproviderselector

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.duchastel.simon.solenne.screens.modelproviderselector.ModelProviderSelectorScreen.Event
import com.duchastel.simon.solenne.screens.modelproviderselector.UiModelProvider.Gemini
import com.duchastel.simon.solenne.ui.components.ModelProviderButton
import com.duchastel.simon.solenne.ui.components.SolenneScaffold
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import solennechatapp.composeapp.generated.resources.Res
import solennechatapp.composeapp.generated.resources.screen_title_select_ai_model

@Composable
fun ModelProviderSelectorUi(
    state: ModelProviderSelectorScreen.State,
    modifier: Modifier,
) {
    val eventSink = state.eventSink

    SolenneScaffold(
        title = stringResource(Res.string.screen_title_select_ai_model),
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
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

@Preview
@Composable
internal fun ModelSelectorUi_Preview() {
    val models = persistentListOf(
        Gemini,
    )

    ModelProviderSelectorUi(
        modifier = Modifier,
        state = ModelProviderSelectorScreen.State(
            models = models,
        )
    )
}
