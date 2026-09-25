package com.codershubinc.nullvoidlauncher.data

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.codershubinc.nullvoidlauncher.R

enum class WidgetFont(val label: String) {
    DEFAULT("Default"),
    SANS_SERIF("Sans-Serif"),
    MONOSPACE("Monospace"),
    SERIF("Serif"),
    CONDENSED("Condensed"),
    DANCING_SCRIPT("Dancing Script (Cursive)"),
    CAVEAT("Caveat (Handwriting)"),
    PACIFICO("Pacifico (Brush Script)"),
    CINZEL("Cinzel (Display Serif)"),
    JETBRAINS_MONO("JetBrains Mono"),
    OUTFIT("Outfit (Modern)"),
    PLAYFAIR("Playfair Display");

    fun toFontFamily(): FontFamily {
        return when (this) {
            DEFAULT -> FontFamily.Default
            SANS_SERIF -> FontFamily(Font(R.font.font_sans))
            MONOSPACE -> FontFamily(Font(R.font.font_monospace))
            SERIF -> FontFamily(Font(R.font.font_serif))
            CONDENSED -> FontFamily(Font(R.font.font_condensed))
            DANCING_SCRIPT -> FontFamily(
                Font(R.font.font_dancing_script, FontWeight.Normal),
                Font(R.font.font_dancing_script_bold, FontWeight.Bold)
            )
            CAVEAT -> FontFamily(
                Font(R.font.font_caveat, FontWeight.Normal),
                Font(R.font.font_caveat_bold, FontWeight.Bold)
            )
            PACIFICO -> FontFamily(Font(R.font.font_pacifico))
            CINZEL -> FontFamily(
                Font(R.font.font_cinzel, FontWeight.Normal),
                Font(R.font.font_cinzel_bold, FontWeight.Bold)
            )
            JETBRAINS_MONO -> FontFamily(
                Font(R.font.font_jetbrains_mono, FontWeight.Normal),
                Font(R.font.font_jetbrains_mono_bold, FontWeight.Bold)
            )
            OUTFIT -> FontFamily(
                Font(R.font.font_outfit, FontWeight.Normal),
                Font(R.font.font_outfit_bold, FontWeight.Bold)
            )
            PLAYFAIR -> FontFamily(
                Font(R.font.font_playfair, FontWeight.Normal),
                Font(R.font.font_playfair_bold, FontWeight.Bold)
            )
        }
    }
}
