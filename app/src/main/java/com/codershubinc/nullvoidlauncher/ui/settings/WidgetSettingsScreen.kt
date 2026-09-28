package com.codershubinc.nullvoidlauncher.ui.settings

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.components.ModernCard
import com.codershubinc.nullvoidlauncher.ui.settings.widgets.*

enum class WidgetSubPage {
    NONE,
    CLOCK,
    MUSIC,
    NETWORK,
    POWER,
    BLUETOOTH,
    FAVORITES,
    DAY,
    STORAGE,
    WEATHER,
    STEPS,
    CONTROL_DECK,
    APPEARANCE,
    DRAWER
}

@Composable
fun WidgetSettingsScreen(
    userManager: UserManager,
    initialPage: WidgetSubPage = WidgetSubPage.NONE,
    onClose: () -> Unit
) {
    var activeSubPage by remember(initialPage) { mutableStateOf(initialPage) }

    val handleBack: () -> Unit = {
        if (initialPage != WidgetSubPage.NONE) {
            onClose()
        } else {
            activeSubPage = WidgetSubPage.NONE
        }
    }

    when (activeSubPage) {
        WidgetSubPage.CLOCK -> {
            ClockWidgetTweaksPage(userManager = userManager, onBack = handleBack)
        }
        WidgetSubPage.MUSIC -> {
            MusicWidgetTweaksPage(userManager = userManager, onBack = handleBack)
        }
        WidgetSubPage.NETWORK -> {
            NetworkWidgetTweaksPage(userManager = userManager, onBack = handleBack)
        }
        WidgetSubPage.POWER -> {
            PowerWidgetTweaksPage(userManager = userManager, onBack = handleBack)
        }
        WidgetSubPage.BLUETOOTH -> {
            BluetoothWidgetTweaksPage(userManager = userManager, onBack = handleBack)
        }
        WidgetSubPage.FAVORITES -> {
            FavoritesWidgetTweaksPage(userManager = userManager, onBack = handleBack)
        }
        WidgetSubPage.DAY -> {
            DayWidgetTweaksPage(userManager = userManager, onBack = handleBack)
        }
        WidgetSubPage.STORAGE -> {
            StorageWidgetTweaksPage(userManager = userManager, onBack = handleBack)
        }
        WidgetSubPage.WEATHER -> {
            WeatherWidgetTweaksPage(userManager = userManager, onBack = handleBack)
        }
        WidgetSubPage.STEPS -> {
            StepsWidgetTweaksPage(userManager = userManager, onBack = handleBack)
        }
        WidgetSubPage.CONTROL_DECK -> {
            ControlDeckWidgetTweaksPage(userManager = userManager, onBack = handleBack)
        }
        WidgetSubPage.APPEARANCE -> {
            AppearanceTweaksPage(userManager = userManager, onBack = handleBack)
        }
        WidgetSubPage.DRAWER -> {
            DrawerTweaksPage(userManager = userManager, onBack = handleBack)
        }
        WidgetSubPage.NONE -> {
            WidgetHubScreen(
                userManager = userManager,
                onNavigate = { activeSubPage = it },
                onClose = onClose
            )
        }
    }
}

@Composable
private fun WidgetHubScreen(
    userManager: UserManager,
    onNavigate: (WidgetSubPage) -> Unit,
    onClose: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080808))
            .statusBarsPadding()
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Spacer(modifier = Modifier.height(28.dp))
        Text("Widget Tweaks", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text("Select a widget to customize its style, font and layout", color = Color.White.copy(alpha = 0.5f), fontSize = 15.sp)
        Spacer(modifier = Modifier.height(28.dp))

        // Global Appearance Section
        Text(
            text = "Global Appearance",
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
        ModernCard {
            WidgetTweakRowItem(
                icon = Icons.Rounded.AutoAwesome,
                title = "Appearance & Styling",
                subtitle = "Glassmorphism, blur intensity, corner radius & sync fonts",
                badgeText = userManager.getWidgetPreset(),
                onClick = { onNavigate(WidgetSubPage.APPEARANCE) }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Widgets Customization List
        Text(
            text = "Individual Widgets",
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
        ModernCard {
            // Clock
            WidgetTweakRowItem(
                icon = Icons.Rounded.Schedule,
                title = "Clock Widget",
                subtitle = "Style: ${userManager.getClockStyle().name} • Font: ${userManager.getClockFont().label}",
                isActive = userManager.getShowClockWidget(),
                onClick = { onNavigate(WidgetSubPage.CLOCK) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))

            // Music
            WidgetTweakRowItem(
                icon = Icons.Rounded.MusicNote,
                title = "Music Widget",
                subtitle = "Style: ${userManager.getMusicStyle().name} • Font: ${userManager.getMusicFont().label}",
                isActive = userManager.getShowMusicWidget(),
                onClick = { onNavigate(WidgetSubPage.MUSIC) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))

            // Network / Wi-Fi
            WidgetTweakRowItem(
                icon = Icons.Rounded.Wifi,
                title = "Network / Wi-Fi Widget",
                subtitle = "Style: ${userManager.getNetworkStyle().name} • Font: ${userManager.getNetworkFont().label}",
                isActive = userManager.getShowNetworkWidget(),
                onClick = { onNavigate(WidgetSubPage.NETWORK) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))

            // Battery / Power
            WidgetTweakRowItem(
                icon = Icons.Rounded.BatteryChargingFull,
                title = "Battery & Power Widget",
                subtitle = "Style: ${userManager.getPowerStyle().name} • Font: ${userManager.getPowerFont().label}",
                isActive = userManager.getShowPowerWidget(),
                onClick = { onNavigate(WidgetSubPage.POWER) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))

            // Bluetooth
            WidgetTweakRowItem(
                icon = Icons.Rounded.Bluetooth,
                title = "Bluetooth Widget",
                subtitle = "Style: ${userManager.getBluetoothStyle().name} • Font: ${userManager.getBluetoothFont().label}",
                isActive = userManager.getShowBluetoothWidget(),
                onClick = { onNavigate(WidgetSubPage.BLUETOOTH) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))

            // Favorites
            WidgetTweakRowItem(
                icon = Icons.Rounded.Star,
                title = "Favorites Widget",
                subtitle = "Style: ${userManager.getFavoritesStyle().name} • Font: ${userManager.getFavoritesFont().label}",
                isActive = userManager.getShowFavoritesWidget(),
                onClick = { onNavigate(WidgetSubPage.FAVORITES) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))

            // Day
            WidgetTweakRowItem(
                icon = Icons.Rounded.CalendarToday,
                title = "Day Widget",
                subtitle = "Style: ${userManager.getDayStyle().name} • Font: ${userManager.getDayFont().label}",
                isActive = userManager.getShowDayWidget(),
                onClick = { onNavigate(WidgetSubPage.DAY) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))

            // Storage
            WidgetTweakRowItem(
                icon = Icons.Rounded.Storage,
                title = "Storage Widget",
                subtitle = "Style: ${userManager.getStorageStyle().name} • Font: ${userManager.getStorageFont().label}",
                isActive = userManager.getShowStorageWidget(),
                onClick = { onNavigate(WidgetSubPage.STORAGE) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))

            // Weather
            WidgetTweakRowItem(
                icon = Icons.Rounded.WbSunny,
                title = "Weather Widget",
                subtitle = "Style: ${userManager.getWeatherStyle().name} • Font: ${userManager.getWeatherFont().label}",
                isActive = userManager.getShowWeatherWidget(),
                onClick = { onNavigate(WidgetSubPage.WEATHER) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))

            // Steps & Google Fit
            WidgetTweakRowItem(
                icon = Icons.AutoMirrored.Rounded.DirectionsWalk,
                title = "Steps & Google Fit Widget",
                subtitle = "Style: ${userManager.getStepsStyle().name} • Goal: ${userManager.getStepsDailyGoal()} steps",
                isActive = userManager.getShowStepsWidget(),
                onClick = { onNavigate(WidgetSubPage.STEPS) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))

            // Add Direct Google Fit App Widget
            val currentContext = androidx.compose.ui.platform.LocalContext.current
            val isFitInstalled = remember { com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper.isGoogleFitInstalled(currentContext) }
            WidgetTweakRowItem(
                icon = Icons.Rounded.FitnessCenter,
                title = "Add Direct Google Fit App Widget",
                subtitle = if (isFitInstalled) "Pin official Google Fit widget directly to home screen" else "Install Google Fit to pin official widgets",
                isActive = isFitInstalled,
                onClick = {
                    if (isFitInstalled) {
                        val ok = com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper.requestPinGoogleFitWidget(currentContext)
                        if (!ok) {
                            onNavigate(WidgetSubPage.STEPS)
                        }
                    } else {
                        com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper.openGoogleFitOrHealth(currentContext)
                    }
                }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))

            // Quick Control Deck
            WidgetTweakRowItem(
                icon = Icons.Rounded.Tune,
                title = "Quick Control Deck",
                subtitle = "Flashlight, ringer modes and device controls",
                isActive = userManager.getShowControlDeck(),
                onClick = { onNavigate(WidgetSubPage.CONTROL_DECK) }
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(alpha = 0.05f))

            // App Drawer Theme
            WidgetTweakRowItem(
                icon = Icons.Rounded.Search,
                title = "App Drawer Theme",
                subtitle = "Style: ${userManager.getDrawerStyle().name} • Spotlight, Grid, Terminal...",
                isActive = true,
                badgeText = userManager.getDrawerStyle().name,
                onClick = { onNavigate(WidgetSubPage.DRAWER) }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun WidgetTweakRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isActive: Boolean? = null,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (isActive != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isActive) Color(0xFF00E676).copy(alpha = 0.15f)
                                else Color.White.copy(alpha = 0.08f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isActive) "ON" else "OFF",
                            color = if (isActive) Color(0xFF00E676) else Color.White.copy(alpha = 0.5f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                if (badgeText != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(WidgetSettingsAccent.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = WidgetSettingsAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }

        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
    }
}
