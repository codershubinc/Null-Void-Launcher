package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WeatherStyle
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.weather.WeatherHelper
import com.codershubinc.nullvoidlauncher.ui.weather.WeatherInfoState

@Composable
fun WeatherWidget(
    style: WeatherStyle = WeatherStyle.ELEGANT,
    modifier: Modifier = Modifier,
    font: WidgetFont? = null,
    previewInfo: WeatherInfoState? = null,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val userManager = remember { UserManager(context) }
    var prefVersion by remember { mutableIntStateOf(0) }

    DisposableEffect(userManager) {
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            prefVersion++
        }
        userManager.prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            userManager.prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    val effectiveFont = font ?: remember(prefVersion) { userManager.getWeatherFont() }
    val weather = remember { previewInfo ?: WeatherHelper.getWeatherInfo() }

    val gestureModifier = if (onLongClick != null) {
        Modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = { onClick?.invoke() },
                onLongPress = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick.invoke()
                }
            )
        }
    } else Modifier

    val finalModifier = modifier.then(gestureModifier)
    val effectiveClick = if (onLongClick != null) null else onClick

    when (style) {
        WeatherStyle.ELEGANT -> ElegantWeather(weather, effectiveFont, finalModifier, effectiveClick)
        WeatherStyle.MINIMAL -> MinimalWeather(weather, effectiveFont, finalModifier, effectiveClick)
        WeatherStyle.TERMINAL -> TerminalWeather(weather, effectiveFont, finalModifier, effectiveClick)
        WeatherStyle.RETRO -> RetroWeather(weather, effectiveFont, finalModifier, effectiveClick)
    }
}

@Composable
private fun ElegantWeather(
    weather: WeatherInfoState,
    font: WidgetFont,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.White.copy(alpha = 0.03f)
                    )
                )
            )
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.White.copy(alpha = 0.05f)
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.WbSunny,
            contentDescription = null,
            tint = Color(0xFFFFD54F),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "${weather.temperature} • ${weather.condition}",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = font.toFontFamily()
        )
        Text(
            text = "(${weather.humidity} H • ${weather.windSpeed})",
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 10.sp,
            fontFamily = font.toFontFamily()
        )
    }
}

@Composable
private fun MinimalWeather(
    weather: WeatherInfoState,
    font: WidgetFont,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.WbSunny,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "${weather.temperature} ${weather.condition.lowercase()}",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
            fontFamily = font.toFontFamily()
        )
    }
}

@Composable
private fun TerminalWeather(
    weather: WeatherInfoState,
    font: WidgetFont,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF0F1A12))
            .border(1.dp, Color(0xFF00E676).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = "env0: [${weather.temperature}] [${weather.humidity} RH] [${weather.windSpeed}]",
            color = Color(0xFF00E676),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RetroWeather(
    weather: WeatherInfoState,
    font: WidgetFont,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1E1608))
            .border(1.dp, Color(0xFFFFB300).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = "METEO [${weather.temperature} ${weather.condition.uppercase()}]",
            color = Color(0xFFFFB300),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}
