package com.duchastel.simon.solenne.screens.modelproviderconfig

import com.duchastel.simon.solenne.data.ai.AIModelProvider
import com.duchastel.simon.solenne.screens.modelproviderselector.UiModelProvider
import kotlin.test.Test
import kotlin.test.assertEquals

class AiModelProviderMappingTest {

    @Test
    fun `AIModelProvider Gemini maps to UiModelProvider Gemini`() {
        val aiModelProvider = AIModelProvider.Gemini
        val uiModelProvider = aiModelProvider.toUiModel()
        assertEquals(UiModelProvider.Gemini, uiModelProvider)
    }
}