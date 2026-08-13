package com.mocare.app.ui.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

class OdometerVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text.filter { it.isDigit() }
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val formattedText = formatOdometer(originalText)
        val offsetMapping = OdometerOffsetMapping(originalText, formattedText)

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }

    private fun formatOdometer(digits: String): String {
        if (digits.isEmpty()) return ""
        val lastDigit = digits.last()
        val otherDigits = if (digits.length > 1) digits.dropLast(1) else "0"
        
        val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedOther = numberFormat.format(otherDigits.toLongOrNull() ?: 0L)
        
        return "$formattedOther,$lastDigit"
    }

    private inner class OdometerOffsetMapping(
        private val originalText: String,
        private val formattedText: String
    ) : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (originalText.isEmpty()) return 0
            if (offset >= originalText.length) return formattedText.length
            
            var digitsPassed = 0
            for (i in formattedText.indices) {
                if (formattedText[i].isDigit()) {
                    digitsPassed++
                }
                if (digitsPassed == offset) {
                    return i + 1
                }
            }
            return formattedText.length
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (formattedText.isEmpty()) return 0
            if (offset >= formattedText.length) return originalText.length
            
            var digitCount = 0
            for (i in 0 until offset) {
                if (formattedText[i].isDigit()) {
                    digitCount++
                }
            }
            return digitCount.coerceAtMost(originalText.length)
        }
    }
}
