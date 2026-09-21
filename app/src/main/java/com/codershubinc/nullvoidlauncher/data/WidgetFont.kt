package com.codershubinc.nullvoidlauncher.data

import android.graphics.Typeface
import androidx.compose.ui.text.font.FontFamily

enum class WidgetFont(val label: String) {
    DEFAULT("Default"),
    SANS_SERIF("Sans-Serif"),
    MONOSPACE("Monospace"),
    SERIF("Serif"),
    CONDENSED("Condensed"),
    CURSIVE("Cursive"),
    CASUAL("Casual");

    fun toFontFamily(): FontFamily {
        return when (this) {
            DEFAULT -> FontFamily.Default
            SANS_SERIF -> FontFamily.SansSerif
            MONOSPACE -> FontFamily.Monospace
            SERIF -> FontFamily.Serif
            CONDENSED -> FontFamily(Typeface.create("sans-serif-condensed", Typeface.NORMAL))
            CURSIVE -> FontFamily.Cursive
            CASUAL -> FontFamily(Typeface.create("casual", Typeface.NORMAL))
        }
    }
}
