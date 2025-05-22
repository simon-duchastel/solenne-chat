package com.duchastel.simon.solenne.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser

/**
 * Converts markdown text to an AnnotatedString.
 *
 * @param markdown The markdown text to convert
 * @return An AnnotatedString representing the styled markdown content
 */
fun AnnotatedString.Companion.fromMarkdown(markdown: String): AnnotatedString {
    val flavor = CommonMarkFlavourDescriptor()
    val parsedTree = MarkdownParser(flavor).buildMarkdownTreeFromString(markdown)
    val html = HtmlGenerator(markdown, parsedTree, flavor).generateHtml()
    
    return AnnotatedString.fromHtml(html)
}