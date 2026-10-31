package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.data.model.WeatherStyle
import com.codershubinc.nullvoidlauncher.data.prefs.rememberPrefVersion
import com.codershubinc.nullvoidlauncher.system.weather.WeatherHelper
import com.codershubinc.nullvoidlauncher.system.weather.WeatherInfoState
import com.codershubinc.nullvoidlauncher.ui.widgets.weather.ElegantWeatherWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.weather.MinimalWeatherWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.weather.RetroWeatherWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.weather.TerminalWeatherWidget

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
    val prefVersion = rememberPrefVersion(userManager)

    val effectiveFont = font ?: remember(prefVersion) { userManager.widgets.weatherFont }
    val weather = previewInfo ?: remember { WeatherHelper.getWeatherInfo() }

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
        WeatherStyle.ELEGANT -> ElegantWeatherWidget(weather, effectiveFont, finalModifier, effectiveClick)
        WeatherStyle.MINIMAL -> MinimalWeatherWidget(weather, effectiveFont, finalModifier, effectiveClick)
        WeatherStyle.TERMINAL -> TerminalWeatherWidget(weather, effectiveFont, finalModifier, effectiveClick)
        WeatherStyle.RETRO -> RetroWeatherWidget(weather, effectiveFont, finalModifier, effectiveClick)
    }
}

// Backward-compatible delegates
@Composable
fun ElegantWeather(weather: WeatherInfoState, font: WidgetFont = WidgetFont.DEFAULT, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) =
    ElegantWeatherWidget(weather, font, modifier, onClick)

@Composable
fun MinimalWeather(weather: WeatherInfoState, font: WidgetFont = WidgetFont.DEFAULT, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) =
    MinimalWeatherWidget(weather, font, modifier, onClick)

@Composable
fun TerminalWeather(weather: WeatherInfoState, font: WidgetFont = WidgetFont.MONOSPACE, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) =
    TerminalWeatherWidget(weather, font, modifier, onClick)

@Composable
fun RetroWeather(weather: WeatherInfoState, font: WidgetFont = WidgetFont.MONOSPACE, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) =
    RetroWeatherWidget(weather, font, modifier, onClick)
