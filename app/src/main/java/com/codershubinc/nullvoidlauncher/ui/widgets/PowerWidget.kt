package com.codershubinc.nullvoidlauncher.ui.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.codershubinc.nullvoidlauncher.data.PowerStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.power.PowerHelper
import com.codershubinc.nullvoidlauncher.ui.power.PowerInfoState
import com.codershubinc.nullvoidlauncher.ui.widgets.power.ElegantPowerWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.power.GaugeBarPowerWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.power.MinimalPowerWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.power.RetroPowerWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.power.RingPowerWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.power.TerminalPowerWidget

/**
 * PowerWidget — Displays real-time battery percentage, charging state, temperature, voltage,
 * and battery saver status.
 * Routes to user-selected styles: ELEGANT, MINIMAL, TERMINAL, RETRO.
 * Interacts with system battery settings on click.
 */
@Composable
fun PowerWidget(
    style: PowerStyle = PowerStyle.ELEGANT,
    modifier: Modifier = Modifier,
    font: WidgetFont? = null,
    previewInfo: PowerInfoState? = null,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val effectiveFont = font ?: userManager.getPowerFont()
    var powerInfo by remember { mutableStateOf(previewInfo ?: PowerHelper.getPowerInfo(context)) }

    DisposableEffect(previewInfo) {
        if (previewInfo != null) {
            powerInfo = previewInfo
            return@DisposableEffect onDispose {}
        }

        // Initialize immediately
        powerInfo = PowerHelper.getPowerInfo(context)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                powerInfo = PowerHelper.getPowerInfo(context)
            }
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        }
        context.registerReceiver(receiver, filter)

        onDispose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {}
        }
    }

    when (style) {
        PowerStyle.ELEGANT   -> ElegantPowerWidget(
            powerInfo = powerInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = onTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        PowerStyle.MINIMAL   -> MinimalPowerWidget(
            powerInfo = powerInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = onTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        PowerStyle.GAUGE_BAR -> GaugeBarPowerWidget(
            powerInfo = powerInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = onTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        PowerStyle.RING      -> RingPowerWidget(
            powerInfo = powerInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = onTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        PowerStyle.TERMINAL  -> TerminalPowerWidget(
            powerInfo = powerInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = onTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        PowerStyle.RETRO     -> RetroPowerWidget(
            powerInfo = powerInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = onTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
    }
}
