package com.duchastel.simon.solenne.tests.util

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.duchastel.simon.solenne.util.parseMarkdownToAnnotatedString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class MarkdownExtensionsTest {

    @Test
    fun fromMarkdown_correctlyConvertsBasicFormatting() {
        val markdown = "This is **bold** and *italic* text"
        val result = parseMarkdownToAnnotatedString(markdown)

        assertEquals("This is bold and italic text", result.text)

        val boldStart = result.text.indexOf("bold")
        val boldEnd = boldStart + "bold".length
        val italicStart = result.text.indexOf("italic")
        val italicEnd = italicStart + "italic".length

        // Check if any style spans apply to the "bold" portion
        val boldSpans = result.spanStyles.filter { span ->
            span.start <= boldStart && span.end >= boldEnd
        }
        assertTrue(
            actual = boldSpans.isNotEmpty(),
            message = "No styles found for the bold text",
        )
        assertTrue(
            actual = boldSpans.any { span -> span.item.fontWeight == FontWeight.Bold },
            message = "Bold style not found"
        )

        // Check if any style spans apply to the "italic" portion
        val italicSpans = result.spanStyles.filter { span ->
            span.start <= italicStart && span.end >= italicEnd
        }
        assertTrue(
            actual = italicSpans.isNotEmpty(),
            message = "No styles found for the italic text"
        )
        assertTrue(
            actual = italicSpans.any { span -> span.item.fontStyle == FontStyle.Italic },
            message = "Italic style not found",
        )
    }

    @Test
    fun fromMarkdown_handlesHeadersCorrectly() {
        val markdown = "# Heading 1\n## Heading 2"
        val result = parseMarkdownToAnnotatedString(markdown)

        // Check that the text content is preserved
        assertTrue(
            actual = result.text.contains("Heading 1"),
            message = "Heading 1 text missing",
        )
        assertTrue(
            actual = result.text.contains("Heading 2"),
            message = "Heading 2 text missing",
        )

        // Headers typically have larger font sizes and/or different font weights
        // Since the exact styling depends on the HTML rendering, we just check
        // that there are style spans affecting the header text
        val heading1Start = result.text.indexOf("Heading 1")
        val heading1End = heading1Start + "Heading 1".length
        val heading1Spans = result.spanStyles.filter { span ->
            span.start <= heading1Start && span.end >= heading1End
        }

        assertTrue(
            actual = heading1Spans.isNotEmpty(),
            message = "No styles found for Heading 1",
        )
    }

    @Test
    fun fromMarkdown_NoOpsForPlainText() {
        val markdown = "This is regular text with no markdown"
        val result = parseMarkdownToAnnotatedString(markdown)

        assertEquals("This is regular text with no markdown", result.text)
        assertTrue(
            actual = result.spanStyles.isEmpty(),
            message = "Unexpected styles found in plain text: ${result.spanStyles}",
        )
    }

    @Test
    fun fromMarkdown_HandlesNewLines() {
        val markdown = "First line\nSecond line\n\n\nThird line way below"
        val result = parseMarkdownToAnnotatedString(markdown)

        assertEquals(
            expected = "First line\nSecond line\n\n\nThird line way below",
            actual = result.text
        )
        assertTrue(
            actual = result.spanStyles.isEmpty(),
            message = "Unexpected styles found in plain text: ${result.spanStyles}",
        )
    }
}
