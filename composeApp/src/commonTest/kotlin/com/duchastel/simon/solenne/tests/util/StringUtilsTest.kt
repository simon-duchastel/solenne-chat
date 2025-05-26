package com.duchastel.simon.solenne.tests.util

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.duchastel.simon.solenne.util.parseHtmlToAnnotatedString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StringUtilsTest {

    @Test
    fun parseHtmlToAnnotatedString_HandlesBodyAndPTag() {
        val html = "<body><p>explain dune</p></body>"

        val result = parseHtmlToAnnotatedString(html)

        assertEquals("explain dune", result.text)
    }

    @Test
    fun parseHtmlToAnnotatedString_handlesBasicFormatting() {
        val html = "This is <b>bold</b> and <i>italic</i> text"
        val result = parseHtmlToAnnotatedString(html)

        assertEquals("This is bold and italic text", result.text)

        val boldStart = result.text.indexOf("bold")
        val boldEnd = boldStart + "bold".length
        val italicStart = result.text.indexOf("italic")
        val italicEnd = italicStart + "italic".length

        // Check bold formatting
        val boldSpans = result.spanStyles.filter { span ->
            span.start <= boldStart && span.end >= boldEnd
        }
        assertTrue(
            actual = boldSpans.isNotEmpty(),
            message = "No styles found for the bold text"
        )
        assertTrue(
            actual = boldSpans.any { span -> span.item.fontWeight == FontWeight.Bold },
            message = "Bold style not found"
        )

        // Check italic formatting
        val italicSpans = result.spanStyles.filter { span ->
            span.start <= italicStart && span.end >= italicEnd
        }
        assertTrue(
            actual = italicSpans.isNotEmpty(),
            message = "No styles found for the italic text"
        )
        assertTrue(
            actual = italicSpans.any { span -> span.item.fontStyle == FontStyle.Italic },
            message = "Italic style not found"
        )
    }

    @Test
    fun parseHtmlToAnnotatedString_handlesStrongAndEm() {
        val html = "Text with <strong>strong</strong> and <em>emphasis</em>"
        val result = parseHtmlToAnnotatedString(html)

        assertEquals("Text with strong and emphasis", result.text)

        val strongStart = result.text.indexOf("strong")
        val strongEnd = strongStart + "strong".length
        val emStart = result.text.indexOf("emphasis")
        val emEnd = emStart + "emphasis".length

        // Check strong formatting
        val strongSpans = result.spanStyles.filter { span ->
            span.start <= strongStart && span.end >= strongEnd
        }
        assertTrue(
            actual = strongSpans.any { span -> span.item.fontWeight == FontWeight.Bold },
            message = "Strong style not found"
        )

        // Check emphasis formatting
        val emSpans = result.spanStyles.filter { span ->
            span.start <= emStart && span.end >= emEnd
        }
        assertTrue(
            actual = emSpans.any { span -> span.item.fontStyle == FontStyle.Italic },
            message = "Emphasis style not found"
        )
    }

    @Test
    fun parseHtmlToAnnotatedString_handlesUnderline() {
        val html = "This has <u>underlined</u> text"
        val result = parseHtmlToAnnotatedString(html)

        assertEquals("This has underlined text", result.text)

        val underlineStart = result.text.indexOf("underlined")
        val underlineEnd = underlineStart + "underlined".length

        val underlineSpans = result.spanStyles.filter { span ->
            span.start <= underlineStart && span.end >= underlineEnd
        }
        assertTrue(
            actual = underlineSpans.any { span -> span.item.textDecoration == TextDecoration.Underline },
            message = "Underline style not found"
        )
    }

    @Test
    fun parseHtmlToAnnotatedString_handlesParagraphs() {
        val html = "<p>First paragraph</p><p>Second paragraph</p>"
        val result = parseHtmlToAnnotatedString(html)

        assertTrue(
            actual = result.text.contains("First paragraph"),
            message = "First paragraph text missing"
        )
        assertTrue(
            actual = result.text.contains("Second paragraph"),
            message = "Second paragraph text missing"
        )
        assertTrue(
            actual = result.text.contains("\n"),
            message = "Newline not found between paragraphs"
        )
    }

    @Test
    fun parseHtmlToAnnotatedString_handlesLineBreaks() {
        val html = "Line 1<br>Line 2<br/>Line 3"
        val result = parseHtmlToAnnotatedString(html)

        assertEquals("Line 1\nLine 2\nLine 3", result.text)
    }

    @Test
    fun parseHtmlToAnnotatedString_handlesUnorderedLists() {
        val html = "<ul><li>Item 1</li><li>Item 2</li><li>Item 3</li></ul>"
        val result = parseHtmlToAnnotatedString(html)

        assertTrue(
            actual = result.text.contains("• Item 1"),
            message = "First list item with bullet not found"
        )
        assertTrue(
            actual = result.text.contains("• Item 2"),
            message = "Second list item with bullet not found"
        )
        assertTrue(
            actual = result.text.contains("• Item 3"),
            message = "Third list item with bullet not found"
        )
    }

    @Test
    fun parseHtmlToAnnotatedString_handlesNestedLists() {
        val html = "<ul><li>Item 1<ul><li>Nested item</li></ul></li><li>Item 2</li></ul>"
        val result = parseHtmlToAnnotatedString(html)

        assertTrue(
            actual = result.text.contains("• Item 1"),
            message = "First level item not found"
        )
        assertTrue(
            actual = result.text.contains("    • Nested item"),
            message = "Nested item with proper indentation not found"
        )
        assertTrue(
            actual = result.text.contains("• Item 2"),
            message = "Second item not found"
        )
    }

    @Test
    fun parseHtmlToAnnotatedString_handlesDiv() {
        val html = "<div>First div</div><div>Second div</div>"
        val result = parseHtmlToAnnotatedString(html)

        assertTrue(
            actual = result.text.contains("First div"),
            message = "First div text missing"
        )
        assertTrue(
            actual = result.text.contains("Second div"),
            message = "Second div text missing"
        )
        assertTrue(
            actual = result.text.contains("\n"),
            message = "Newline not found between divs"
        )
    }

    @Test
    fun parseHtmlToAnnotatedString_handlesPlainText() {
        val html = "This is plain text with no HTML"
        val result = parseHtmlToAnnotatedString(html)

        assertEquals("This is plain text with no HTML", result.text)
        assertTrue(
            actual = result.spanStyles.isEmpty(),
            message = "Unexpected styles found in plain text"
        )
    }

    @Test
    fun parseHtmlToAnnotatedString_handlesEmptyString() {
        val html = ""
        val result = parseHtmlToAnnotatedString(html)

        assertEquals("", result.text)
        assertTrue(
            actual = result.spanStyles.isEmpty(),
            message = "Unexpected styles found in empty string"
        )
    }

    @Test
    fun parseHtmlToAnnotatedString_handlesCombinedFormatting() {
        val html = "<p>This is <b><i>bold and italic</i></b> text</p>"
        val result = parseHtmlToAnnotatedString(html)

        assertTrue(
            actual = result.text.contains("bold and italic"),
            message = "Combined formatting text missing"
        )

        val combinedStart = result.text.indexOf("bold and italic")
        val combinedEnd = combinedStart + "bold and italic".length

        val combinedSpans = result.spanStyles.filter { span ->
            span.start <= combinedStart && span.end >= combinedEnd
        }

        assertTrue(
            actual = combinedSpans.any { span -> span.item.fontWeight == FontWeight.Bold },
            message = "Bold style not found in combined formatting"
        )
        assertTrue(
            actual = combinedSpans.any { span -> span.item.fontStyle == FontStyle.Italic },
            message = "Italic style not found in combined formatting"
        )
    }
}