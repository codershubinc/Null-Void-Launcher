package com.codershubinc.nullvoidlauncher

import com.codershubinc.nullvoidlauncher.utils.AppVersion
import com.codershubinc.nullvoidlauncher.utils.MathEvaluator
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testMathEvaluator_basicOperations() {
        assertEquals("4", MathEvaluator.evaluate("2 + 2"))
        assertEquals("7", MathEvaluator.evaluate("10 - 3"))
        assertEquals("20", MathEvaluator.evaluate("4 * 5"))
        assertEquals("5", MathEvaluator.evaluate("20 / 4"))
        assertEquals("14", MathEvaluator.evaluate("2 + 3 * 4"))
        assertEquals("20", MathEvaluator.evaluate("(2 + 3) * 4"))
    }

    @Test
    fun testMathEvaluator_divisionByZeroAndEdgeCases() {
        assertNull(MathEvaluator.evaluate("10 / 0"))
        assertNull(MathEvaluator.evaluate(""))
        assertNull(MathEvaluator.evaluate("abc"))
        assertNull(MathEvaluator.evaluate("hello + world"))
        assertNull(MathEvaluator.evaluate("+++++"))
    }

    @Test
    fun testMathEvaluator_recursionDepthSafety() {
        // 50 nested parentheses must not crash with StackOverflowError
        val deepNested = "(".repeat(50) + "1" + ")".repeat(50) + " + 1"
        assertNull(MathEvaluator.evaluate(deepNested))
    }

    @Test
    fun testAppVersion_parsingAndComparison() {
        val v012 = AppVersion.parse("v0.1.2")
        val v013 = AppVersion.parse("v0.1.3")
        val v013Beta = AppVersion.parse("v0.1.3-beta")
        val v014 = AppVersion.parse("v0.1.4")

        assertEquals(0, v012.major)
        assertEquals(1, v012.minor)
        assertEquals(2, v012.patch)

        assertEquals(0, v014.major)
        assertEquals(1, v014.minor)
        assertEquals(4, v014.patch)

        assertTrue(v014 > v013)
        assertTrue(v013 > v012)
        assertTrue(v013 > v013Beta)
        assertTrue(v013Beta > v012)
    }

    @Test
    fun testFontManager_sanitizeFontFileName() {
        assertEquals("outfit.ttf", com.codershubinc.nullvoidlauncher.data.fonts.FontManager.sanitizeFontFileName("Outfit"))
        assertEquals("jetbrains_mono.ttf", com.codershubinc.nullvoidlauncher.data.fonts.FontManager.sanitizeFontFileName("JetBrains Mono"))
        assertEquals("playfair_display.ttf", com.codershubinc.nullvoidlauncher.data.fonts.FontManager.sanitizeFontFileName("Playfair Display"))
        assertEquals("dancing_script.ttf", com.codershubinc.nullvoidlauncher.data.fonts.FontManager.sanitizeFontFileName("Dancing Script (Cursive)"))
    }

    @Test
    fun testWidgetFont_systemVsGoogleFontFlags() {
        assertFalse(com.codershubinc.nullvoidlauncher.data.WidgetFont.DEFAULT.isGoogleFont)
        assertFalse(com.codershubinc.nullvoidlauncher.data.WidgetFont.SANS_SERIF.isGoogleFont)
        assertFalse(com.codershubinc.nullvoidlauncher.data.WidgetFont.MONOSPACE.isGoogleFont)
        assertFalse(com.codershubinc.nullvoidlauncher.data.WidgetFont.SERIF.isGoogleFont)
        assertFalse(com.codershubinc.nullvoidlauncher.data.WidgetFont.CONDENSED.isGoogleFont)

        assertTrue(com.codershubinc.nullvoidlauncher.data.WidgetFont.OUTFIT.isGoogleFont)
        assertTrue(com.codershubinc.nullvoidlauncher.data.WidgetFont.JETBRAINS_MONO.isGoogleFont)
        assertTrue(com.codershubinc.nullvoidlauncher.data.WidgetFont.INTER.isGoogleFont)
        assertEquals("Outfit", com.codershubinc.nullvoidlauncher.data.WidgetFont.OUTFIT.googleFontFamily)
        assertEquals("JetBrains Mono", com.codershubinc.nullvoidlauncher.data.WidgetFont.JETBRAINS_MONO.googleFontFamily)
    }

    @Test
    fun testGoogleFontDownloader_candidateResolution() {
        val candidates = com.codershubinc.nullvoidlauncher.data.fonts.GoogleFontDownloader.getCandidateNames(
            "Playwrite Belgique Wallonie-Bruxelles Guides"
        )
        assertTrue(candidates.contains("Playwrite BE WAL Guides"))

        val redHatCandidates = com.codershubinc.nullvoidlauncher.data.fonts.GoogleFontDownloader.getCandidateNames(
            "Red-Hat-Display"
        )
        assertTrue(redHatCandidates.contains("Red Hat Display"))

        val dancingCandidates = com.codershubinc.nullvoidlauncher.data.fonts.GoogleFontDownloader.getCandidateNames(
            "Dancing Script (Cursive)"
        )
        assertTrue(dancingCandidates.contains("Dancing Script"))
    }
}