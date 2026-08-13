package com.mocare.app.ui.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        // Hanya format jika string berupa angka
        val numericValue = originalText.toLongOrNull()
        if (numericValue == null) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val symbols = DecimalFormatSymbols(Locale("id", "ID"))
        val formatter = DecimalFormat("#,###", symbols)
        val formattedText = formatter.format(numericValue)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var transformedOffset = 0
                var originalCharsCount = 0
                for (i in formattedText.indices) {
                    if (originalCharsCount == offset) break
                    if (formattedText[i].isDigit()) {
                        originalCharsCount++
                    }
                    transformedOffset++
                }
                // Handle cursor if it's at the very end
                if (originalCharsCount < offset) {
                    transformedOffset += (offset - originalCharsCount)
                }
                return transformedOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                var originalOffset = 0
                for (i in 0 until offset) {
                    if (i < formattedText.length && formattedText[i].isDigit()) {
                        originalOffset++
                    }
                }
                return originalOffset
            }
        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}
