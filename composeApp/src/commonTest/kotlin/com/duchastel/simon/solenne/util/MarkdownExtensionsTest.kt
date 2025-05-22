package com.duchastel.simon.solenne.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MarkdownExtensionsTest {

    @Test
    fun `fromMarkdown correctly converts basic formatting`() {
        val markdown = "This is **bold** and *italic* text"
        val result = parseMarkdownToAnnotatedString(markdown)

        assertEquals("This is bold and italic text", result.text)

        val boldStart = result.text.indexOf("bold")
        val boldEnd = boldStart + "bold".length
        val italicStart = result.text.indexOf("italic")
        val italicEnd = italicStart + "italic".length

        // Check if any style spans apply to the "bold" portion
        val boldSpans = result.spanStyles.filter {
            it.start <= boldStart && it.end >= boldEnd
        }
        assertTrue(boldSpans.isNotEmpty(), "No styles found for the bold text")
        assertTrue(boldSpans.any { it.item.fontWeight == FontWeight.Bold }, "Bold style not found")

        // Check if any style spans apply to the "italic" portion
        val italicSpans = result.spanStyles.filter {
            it.start <= italicStart && it.end >= italicEnd
        }
        assertTrue(italicSpans.isNotEmpty(), "No styles found for the italic text")
        assertTrue(
            italicSpans.any { it.item.fontStyle == FontStyle.Italic },
            "Italic style not found"
        )
    }

    @Test
    fun `fromMarkdown handles headers correctly`() {
        val markdown = "# Heading 1\n## Heading 2"
        val result = parseMarkdownToAnnotatedString(markdown)

        // Check that the text content is preserved
        assertTrue(result.text.contains("Heading 1"), "Heading 1 text missing")
        assertTrue(result.text.contains("Heading 2"), "Heading 2 text missing")

        // Headers typically have larger font sizes and/or different font weights
        // Since the exact styling depends on the HTML rendering, we just check
        // that there are style spans affecting the header text
        val heading1Start = result.text.indexOf("Heading 1")
        val heading1End = heading1Start + "Heading 1".length
        val heading1Spans = result.spanStyles.filter {
            it.start <= heading1Start && it.end >= heading1End
        }

        assertTrue(heading1Spans.isNotEmpty(), "No styles found for Heading 1")
    }
}