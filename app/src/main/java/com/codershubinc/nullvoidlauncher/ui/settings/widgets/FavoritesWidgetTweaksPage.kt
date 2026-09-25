package com.codershubinc.nullvoidlauncher.ui.settings.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.FavoritesStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.repository.AppInfo
import com.codershubinc.nullvoidlauncher.data.repository.getInstalledApps
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.widgets.FavoritesWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun FavoritesWidgetTweaksPage(
    userManager: UserManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showFavorites by remember { mutableStateOf(userManager.getShowFavoritesWidget()) }
    var favoritesStyle by remember { mutableStateOf(userManager.getFavoritesStyle()) }
    var favoritesFont by remember { mutableStateOf(userManager.getFavoritesFont()) }

    var previewApps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            previewApps = getInstalledApps(context).take(4)
        }
    }

    WidgetPageScaffold(
        title = "Favorites Widget",
        subtitle = "Dock, grid and minimal pinned app styles",
        onBack = onBack
    ) {
        WidgetVisibilityCard(
            title = "Show Favorites Widget",
            description = "Display your favorite apps quick-access widget on home screen",
            visible = showFavorites,
            onVisibleChange = {
                showFavorites = it
                userManager.saveShowFavoritesWidget(it)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        ModernCard {
            Text(
                text = "Live Preview",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.03f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                FavoritesWidget(
                    style = favoritesStyle,
                    font = favoritesFont,
                    previewApps = previewApps
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            WidgetStyleSelector(
                title = "Favorites Style",
                styles = FavoritesStyle.entries,
                selectedStyle = favoritesStyle,
                onStyleSelected = {
                    favoritesStyle = it
                    userManager.saveFavoritesStyle(it)
                },
                getLabel = { it.name }
            )

            WidgetFontSelector(
                selectedFont = favoritesFont,
                onFontSelected = {
                    favoritesFont = it
                    userManager.saveFavoritesFont(it)
                }
            )
        }
    }
}
