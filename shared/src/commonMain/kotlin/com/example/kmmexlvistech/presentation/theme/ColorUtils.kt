package com.example.kmmexlvistech.presentation.theme

import androidx.compose.ui.graphics.Color

fun parseHexColor(hex: String?, fallback: Color = Color(0xFF2196F3)): Color {
    if (hex.isNullOrBlank()) return fallback
    return try {
        val cleaned = hex.removePrefix("#")
        val colorLong = cleaned.toLong(16)
        when (cleaned.length) {
            6 -> Color(colorLong or 0x00000000FF000000L.let { 0xFF000000 } or (0xFF000000L and 0xFF000000L) or colorLong or 0xFF000000)
            8 -> Color(colorLong)
            else -> fallback
        }
    } catch (e: Exception) {
        fallback
    }
}

