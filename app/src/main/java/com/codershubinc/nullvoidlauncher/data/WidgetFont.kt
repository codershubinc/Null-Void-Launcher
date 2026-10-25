package com.codershubinc.nullvoidlauncher.data

import android.graphics.Typeface
import androidx.compose.ui.text.font.FontFamily
import com.codershubinc.nullvoidlauncher.data.fonts.FontManager

enum class WidgetFont(
    val label: String,
    val isGoogleFont: Boolean = false,
    val googleFontFamily: String? = null
) {
    DEFAULT("Default"),
    SANS_SERIF("Sans-Serif"),
    MONOSPACE("Monospace"),
    SERIF("Serif"),
    CONDENSED("Condensed"),
    DANCING_SCRIPT("Dancing Script (Cursive)", isGoogleFont = true, googleFontFamily = "Dancing Script"),
    CAVEAT("Caveat (Handwriting)", isGoogleFont = true, googleFontFamily = "Caveat"),
    PACIFICO("Pacifico (Brush Script)", isGoogleFont = true, googleFontFamily = "Pacifico"),
    CINZEL("Cinzel (Display Serif)", isGoogleFont = true, googleFontFamily = "Cinzel"),
    JETBRAINS_MONO("JetBrains Mono", isGoogleFont = true, googleFontFamily = "JetBrains Mono"),
    OUTFIT("Outfit (Modern)", isGoogleFont = true, googleFontFamily = "Outfit"),
    PLAYFAIR("Playfair Display", isGoogleFont = true, googleFontFamily = "Playfair Display"),
    INTER("Inter (Clean Sans)", isGoogleFont = true, googleFontFamily = "Inter"),
    POPPINS("Poppins (Geometric)", isGoogleFont = true, googleFontFamily = "Poppins"),
    FIRA_CODE("Fira Code (Code Mono)", isGoogleFont = true, googleFontFamily = "Fira Code"),
    SPACE_GROTESK("Space Grotesk (Tech)", isGoogleFont = true, googleFontFamily = "Space Grotesk"),
    BEBAS_NEUE("Bebas Neue (Condensed)", isGoogleFont = true, googleFontFamily = "Bebas Neue"),
    LOBSTER("Lobster (Retro)", isGoogleFont = true, googleFontFamily = "Lobster"),
    COMFORTAA("Comfortaa (Rounded)", isGoogleFont = true, googleFontFamily = "Comfortaa"),
    ROBOTO_SLAB("Roboto Slab (Slab Serif)", isGoogleFont = true, googleFontFamily = "Roboto Slab"),
    CUSTOM("Custom Google Font", isGoogleFont = true);

    fun toFontFamily(): FontFamily {
        return when (this) {
            DEFAULT -> FontFamily.Default
            SANS_SERIF -> FontFamily.SansSerif
            MONOSPACE -> FontFamily.Monospace
            SERIF -> FontFamily.Serif
            CONDENSED -> try {
                FontFamily(Typeface.create("sans-serif-condensed", Typeface.NORMAL))
            } catch (e: Exception) {
                FontFamily.SansSerif
            }
            CUSTOM -> FontManager.getCustomFontFamily()
            else -> FontManager.getFontFamily(this)
        }
    }
}
