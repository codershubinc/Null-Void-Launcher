package com.codershubinc.nullvoidlauncher.data.wallpaper

import android.content.Context
import android.widget.Toast
import androidx.core.content.edit
import com.codershubinc.nullvoidlauncher.data.UserManager

data class CloudWallpaper(
    val id: String,
    val name: String,
    val url: String
)

/**
 * CloudWallpaperEngine — Zero-backend Cloudflare R2 / CDN cloud wallpaper engine.
 * Allows instant cycling through curated dark, minimalist, and void-aesthetic wallpapers.
 */
object CloudWallpaperEngine {

    val curatedWallpapers = listOf(
        CloudWallpaper(
            id = "obsidian_void",
            name = "Obsidian Void",
            url = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=1600&q=80"
        ),
        CloudWallpaper(
            id = "event_horizon",
            name = "Event Horizon",
            url = "https://images.unsplash.com/photo-1507499739999-097706ad8914?auto=format&fit=crop&w=1600&q=80"
        ),
        CloudWallpaper(
            id = "minimal_waves",
            name = "Dark Geometry",
            url = "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?auto=format&fit=crop&w=1600&q=80"
        ),
        CloudWallpaper(
            id = "cyber_noir",
            name = "Cyber Noir",
            url = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?auto=format&fit=crop&w=1600&q=80"
        ),
        CloudWallpaper(
            id = "black_dunes",
            name = "Black Dunes",
            url = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=1600&q=80"
        ),
        CloudWallpaper(
            id = "indigo_aurora",
            name = "Indigo Aurora",
            url = "https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=1600&q=80"
        )
    )

    fun cycleNextWallpaper(context: Context, userManager: UserManager): CloudWallpaper {
        val prefs = context.getSharedPreferences("cloud_wallpaper_prefs", Context.MODE_PRIVATE)
        val currentIndex = prefs.getInt("cloud_wallpaper_idx", -1)
        val nextIndex = (currentIndex + 1) % curatedWallpapers.size
        prefs.edit { putInt("cloud_wallpaper_idx", nextIndex) }

        val wallpaper = curatedWallpapers[nextIndex]
        userManager.saveWallpaperUri(wallpaper.url)
        userManager.saveShowWallpaper(true)

        Toast.makeText(context, "Wallpaper: ${wallpaper.name}", Toast.LENGTH_SHORT).show()
        return wallpaper
    }
}
