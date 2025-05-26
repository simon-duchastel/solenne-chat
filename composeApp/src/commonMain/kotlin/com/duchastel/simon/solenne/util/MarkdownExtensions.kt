package com.duchastel.simon.solenne.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

/**
 * Helper function to parse markdown text into an AnnotatedString in compose,
 * where it uses remember to avoid re-parsing the same markdown text on
 * recomposition.
 */
@Composable
fun String.parseMarkdown(): AnnotatedString {
    return remember(this) {
        parseMarkdownToAnnotatedString(this)
    }
}

/**
 * Converts markdown text to an AnnotatedString.
 *
 * @return An AnnotatedString representing the styled markdown content
 */
fun parseMarkdownToAnnotatedString(markdown: String): AnnotatedString {
    val flavor = CommonMarkFlavourDescriptor()
    val parsedTree = MarkdownParser(flavor).buildMarkdownTreeFromString(markdown)
    val html = HtmlGenerator(markdown, parsedTree, flavor).generateHtml()

    return parseHtmlToAnnotatedString(html)
}