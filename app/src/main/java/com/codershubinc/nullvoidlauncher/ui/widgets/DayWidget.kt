package com.codershubinc.nullvoidlauncher.ui.widgets

import android.content.SharedPreferences
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import com.codershubinc.nullvoidlauncher.data.DayStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.widgets.day.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.minutes

@Composable
fun DayWidget(
    modifier: Modifier = Modifier,
    style: DayStyle? = null,
    font: WidgetFont? = null,
    dayText: String? = null,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    val userManager = remember { UserManager(context) }
    var prefVersion by remember { mutableIntStateOf(0) }

    DisposableEffect(userManager) {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            prefVersion++
        }
        userManager.prefs.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            userManager.prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    val effectiveStyle = style ?: remember(prefVersion) { userManager.getDayStyle() }
    val effectiveFont = font ?: remember(prefVersion) { userManager.getDayFont() }

    var currentDay by remember { mutableStateOf(dayText ?: "") }

    LaunchedEffect(dayText) {
        if (dayText == null) {
            val dayFormatter = SimpleDateFormat("EEEE", Locale.getDefault())
            while (true) {
                currentDay = dayFormatter.format(Date()).uppercase()
                delay(1.minutes)
            }
        } else {
            currentDay = dayText
        }
    }

    val gestureModifier = if (onTap != null || onLongClick != null) {
        Modifier.pointerInput(Unit) {
            detectTapGestures(
                onTap = { onTap?.invoke() },
                onLongPress = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick?.invoke()
                }
            )
        }
    } else Modifier

    Box(modifier = modifier.then(gestureModifier)) {
        when (effectiveStyle) {
            DayStyle.ELEGANT   -> ElegantDay(currentDay, font = effectiveFont)
            DayStyle.RETRO     -> RetroDay(currentDay, font = effectiveFont)
            DayStyle.MINIMAL   -> MinimalDay(currentDay, font = effectiveFont)
            DayStyle.MODERN    -> ModernDay(currentDay, font = effectiveFont)
            DayStyle.BRUTALIST -> BrutalistDay(currentDay, font = effectiveFont)
        }
    }
}
