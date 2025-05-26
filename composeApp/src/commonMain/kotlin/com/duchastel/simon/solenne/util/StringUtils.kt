package com.duchastel.simon.solenne.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser

fun parseHtmlToAnnotatedString(html: String): AnnotatedString {
    val annotatedString = buildAnnotatedString {
        val styleStack = mutableListOf<SpanStyle>()
        var listLevel = 0

        val handler = object : KsoupHtmlHandler {
            override fun onOpenTag(name: String, attributes: Map<String, String>, isImplied: Boolean) {
                when (name.lowercase()) {
                    "b", "strong" -> styleStack.add(SpanStyle(fontWeight = FontWeight.Bold))
                    "i", "em" -> styleStack.add(SpanStyle(fontStyle = FontStyle.Italic))
                    "u" -> styleStack.add(SpanStyle(textDecoration = TextDecoration.Underline))

                    "ul" -> {
                        listLevel++
                    }

                    "li" -> {
                        append("\n")
                        append(" ".repeat((listLevel - 1) * 4) + "• ")
                    }
                }
            }

            override fun onCloseTag(name: String, isImplied: Boolean) {
                when (name.lowercase()) {
                    "p", "div" -> append("\n")
                    "ul" -> {
                        listLevel = maxOf(0, listLevel - 1)
                    }
                    "br" -> append("\n")
                    "b", "strong", "i", "em", "u" -> {
                        if (styleStack.isNotEmpty()) {
                            styleStack.removeAt(styleStack.size - 1)
                        }
                    }
                }
            }


            override fun onText(text: String) {
                if (styleStack.isEmpty()) {
                    append(text)
                } else {
                    val combinedStyle = styleStack.reduce { acc, style -> acc.merge(style) }
                    append(AnnotatedString(text, combinedStyle))
                }
            }
        }

        val parser = KsoupHtmlParser(handler)
        parser.write(html)
        parser.end()
    }

    return annotatedString.trim()
}

fun AnnotatedString.trim(): AnnotatedString {
    if (text.isEmpty()) return this

    val trimmedTextEnd = text.trimEnd()
    val trimmedTextStart = text.trimStart()
    val indexOfTrimmedStart = text.length - trimmedTextStart.length
    val indexOfTrimmedEnd = trimmedTextEnd.lastIndex

    // if no trimming occurred, return the same AnnotatedString
    if (indexOfTrimmedStart == 0 && indexOfTrimmedEnd == text.lastIndex) {
        return this
    }

    // Return the subsequence without the trimmed beginning and end
    return subSequence(indexOfTrimmedStart, indexOfTrimmedEnd + 1) // +1 because subSequence is exclusive
}

