package com.example.aos.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

@Composable
fun HighlightedText(
    text: String,
    keywords: String,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    if (keywords.isBlank()) {
        Text(text = text, modifier = modifier)
        return
    }

    val annotatedString = buildAnnotatedString {
        var lastIndex = 0
        val lowerText = text.lowercase()
        val lowerKeywords = keywords.lowercase()
        
        var index = lowerText.indexOf(lowerKeywords)
        while (index != -1) {
            // Add the non-highlighted part
            append(text.substring(lastIndex, index))
            
            // Add the highlighted part
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.error,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(text.substring(index, index + keywords.length))
            }
            
            lastIndex = index + keywords.length
            index = lowerText.indexOf(lowerKeywords, lastIndex)
        }
        
        // Add the remaining text
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }

    Text(text = annotatedString, modifier = modifier)
} 