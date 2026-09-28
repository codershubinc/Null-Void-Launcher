package com.codershubinc.nullvoidlauncher.ui.widgets

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.codershubinc.nullvoidlauncher.data.StepsStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper
import com.codershubinc.nullvoidlauncher.ui.steps.StepsInfoState
import com.codershubinc.nullvoidlauncher.ui.widgets.steps.*

/**
 * StepsWidget — Google Steps / Google Fit Pedometer widget.
 * Supports multiple visual styles: ELEGANT, MINIMAL, RING, GAUGE_BAR, TERMINAL, RETRO.
 * Connects with hardware step sensors and opens Google Fit / Health upon tapping.
 */
@Composable
fun StepsWidget(
    modifier: Modifier = Modifier,
    style: StepsStyle? = null,
    font: WidgetFont? = null,
    previewInfo: StepsInfoState? = null,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
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

    DisposableEffect(context) {
        StepsHelper.registerStepSensor(context)
        StepsHelper.syncSteps(context)
        onDispose {
            // Keep background registration or sensor listener active
        }
    }

    val effectiveStyle = style ?: remember(prefVersion) { userManager.getStepsStyle() }
    val effectiveFont = font ?: remember(prefVersion) { userManager.getStepsFont() }
    val dailyGoal = remember(prefVersion) { userManager.getStepsDailyGoal() }

    val liveStepsInfo by StepsHelper.stepsState.collectAsState()
    Log.d("StepsWidget", "Live steps info: $liveStepsInfo")
    val stepsInfo = previewInfo ?: remember(liveStepsInfo, dailyGoal) {
        if (liveStepsInfo.dailyGoal != dailyGoal) {
            liveStepsInfo.copy(dailyGoal = dailyGoal)
        } else {
            liveStepsInfo
        }
    }

    val handleTap = {
        StepsHelper.syncSteps(context)
        if (onTap != null) onTap()
        else StepsHelper.openGoogleFitOrHealth(context)
    }

    when (effectiveStyle) {
        StepsStyle.ELEGANT   -> ElegantStepsWidget(
            stepsInfo = stepsInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = handleTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        StepsStyle.MINIMAL   -> MinimalStepsWidget(
            stepsInfo = stepsInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = handleTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        StepsStyle.RING      -> RingStepsWidget(
            stepsInfo = stepsInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = handleTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        StepsStyle.GAUGE_BAR -> GaugeBarStepsWidget(
            stepsInfo = stepsInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = handleTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        StepsStyle.TERMINAL  -> TerminalStepsWidget(
            stepsInfo = stepsInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = handleTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        StepsStyle.RETRO     -> RetroStepsWidget(
            stepsInfo = stepsInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = handleTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
    }
}
