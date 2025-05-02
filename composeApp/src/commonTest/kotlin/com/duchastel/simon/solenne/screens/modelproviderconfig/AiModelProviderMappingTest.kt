package com.duchastel.simon.solenne.screens.modelproviderconfig

import com.duchastel.simon.solenne.data.ai.AIModelProvider
import com.duchastel.simon.solenne.screens.modelproviderselector.UiModelProvider
import kotlin.test.Test
import kotlin.test.assertEquals

class AiModelProviderMappingTest {

    @Test
    fun `AIModelProvider OpenAI maps to UiModelProvider OpenAI`() {
        val aiModelProvider = AIModelProvider.OpenAI
        val uiModelProvider = aiModelProvider.toUiModel()
        assertEquals(UiModelProvider.OpenAI, uiModelProvider)
    }

    @Test
    fun `AIModelProvider Anthropic maps to UiModelProvider Anthropic`() {
        val aiModelProvider = AIModelProvider.Anthropic
        val uiModelProvider = aiModelProvider.toUiModel()
        assertEquals(UiModelProvider.Anthropic, uiModelProvider)
    }

    @Test
    fun `AIModelProvider DeepSeek maps to UiModelProvider DeepSeek`() {
        val aiModelProvider = AIModelProvider.DeepSeek
        val uiModelProvider = aiModelProvider.toUiModel()
        assertEquals(UiModelProvider.DeepSeek, uiModelProvider)
    }

    @Test
    fun `AIModelProvider Gemini maps to UiModelProvider Gemini`() {
        val aiModelProvider = AIModelProvider.Gemini
        val uiModelProvider = aiModelProvider.toUiModel()
        assertEquals(UiModelProvider.Gemini, uiModelProvider)
    }

    @Test
    fun `AIModelProvider Grok maps to UiModelProvider Grok`() {
        val aiModelProvider = AIModelProvider.Grok
        val uiModelProvider = aiModelProvider.toUiModel()
        assertEquals(UiModelProvider.Grok, uiModelProvider)
    }
}