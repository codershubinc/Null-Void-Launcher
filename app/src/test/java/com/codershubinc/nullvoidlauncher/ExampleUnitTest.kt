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
        val v015 = AppVersion.parse("v0.1.5")
        val v016 = AppVersion.parse("v0.1.6")

        assertEquals(0, v012.major)
        assertEquals(1, v012.minor)
        assertEquals(2, v012.patch)

        assertEquals(0, v014.major)
        assertEquals(1, v014.minor)
        assertEquals(4, v014.patch)

        assertEquals(0, v015.major)
        assertEquals(1, v015.minor)
        assertEquals(5, v015.patch)

        assertEquals(0, v016.major)
        assertEquals(1, v016.minor)
        assertEquals(6, v016.patch)

        assertTrue(v016 > v015)
        assertTrue(v015 > v014)
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

    @Test
    fun testGetTopHits_prefixAndFavoritesAndFallbacks() {
        val unsafeField = sun.misc.Unsafe::class.java.getDeclaredField("theUnsafe")
        unsafeField.isAccessible = true
        val unsafe = unsafeField.get(null) as sun.misc.Unsafe
        val dummyComponent = try {
            android.content.ComponentName("com.test", "com.test.MainActivity")
        } catch (_: Throwable) {
            unsafe.allocateInstance(android.content.ComponentName::class.java) as android.content.ComponentName
        }
        val dummyUser = unsafe.allocateInstance(android.os.UserHandle::class.java) as android.os.UserHandle

        val app1 = com.codershubinc.nullvoidlauncher.data.repository.AppInfo("Calculator", "com.test.calc", dummyComponent, dummyUser)
        val app2 = com.codershubinc.nullvoidlauncher.data.repository.AppInfo("Camera", "com.test.camera", dummyComponent, dummyUser)
        val app3 = com.codershubinc.nullvoidlauncher.data.repository.AppInfo("Chrome", "com.test.chrome", dummyComponent, dummyUser)
        val app4 = com.codershubinc.nullvoidlauncher.data.repository.AppInfo("Google Maps", "com.test.maps", dummyComponent, dummyUser)
        val app5 = com.codershubinc.nullvoidlauncher.data.repository.AppInfo("YouTube", "com.test.yt", dummyComponent, dummyUser)

        val visibleApps = listOf(app1, app2, app3, app4, app5)
        val favorites = listOf("com.test.calc", "com.test.yt")

        // 1. Idle query (empty) should return favorites
        val idleHits = com.codershubinc.nullvoidlauncher.data.repository.getTopHits("", visibleApps, favorites)
        assertEquals(2, idleHits.size)
        assertEquals("Calculator", idleHits[0].label)
        assertEquals("YouTube", idleHits[1].label)

        // 2. Query starting with "ca" should return Calculator and Camera (prefix matches)
        val caHits = com.codershubinc.nullvoidlauncher.data.repository.getTopHits("ca", visibleApps, favorites)
        assertEquals(2, caHits.size)
        assertEquals("Calculator", caHits[0].label)
        assertEquals("Camera", caHits[1].label)

        // 3. Query starting with "chr" should return Chrome
        val chrHits = com.codershubinc.nullvoidlauncher.data.repository.getTopHits("chr", visibleApps, favorites)
        assertEquals(1, chrHits.size)
        assertEquals("Chrome", chrHits[0].label)

        // 4. Query with leading whitespace & case-insensitivity: "  chRo "
        val mixedHits = com.codershubinc.nullvoidlauncher.data.repository.getTopHits("  chRo ", visibleApps, favorites)
        assertEquals(1, mixedHits.size)
        assertEquals("Chrome", mixedHits[0].label)

        // 5. Query matching word within app name: "maps" -> "Google Maps"
        val mapsHits = com.codershubinc.nullvoidlauncher.data.repository.getTopHits("maps", visibleApps, favorites)
        assertEquals(1, mapsHits.size)
        assertEquals("Google Maps", mapsHits[0].label)

        // 6. Query with no prefix nor word match -> empty
        val noHits = com.codershubinc.nullvoidlauncher.data.repository.getTopHits("xyz", visibleApps, favorites)
        assertTrue(noHits.isEmpty())
    }
}