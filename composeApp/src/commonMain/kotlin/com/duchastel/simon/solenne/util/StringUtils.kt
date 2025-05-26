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

                    "p", "div" -> append("\n")
                    "br" -> append("\n")

                    "ul" -> {
                        listLevel++
                        append("\n")
                    }

                    "li" -> {
                        append("\n")
                        append(" ".repeat((listLevel - 1) * 4) + "• ")
                    }

                    else -> styleStack.add(SpanStyle())
                }
            }

            override fun onCloseTag(name: String, isImplied: Boolean) {
                when (name.lowercase()) {
                    "p", "div" -> append("\n")
                    "ul" -> {
                        listLevel = maxOf(0, listLevel - 1)
                        append("\n")
                    }
                }

                if (styleStack.isNotEmpty()) {
                    styleStack.removeAt(styleStack.size - 1)
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

    return annotatedString
}

