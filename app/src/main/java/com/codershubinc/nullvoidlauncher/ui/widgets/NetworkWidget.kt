package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.codershubinc.nullvoidlauncher.data.NetworkStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.network.NetworkHelper
import com.codershubinc.nullvoidlauncher.ui.network.NetworkInfoState
import com.codershubinc.nullvoidlauncher.ui.widgets.network.ElegantNetworkWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.network.MinimalNetworkWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.network.RetroNetworkWidget
import com.codershubinc.nullvoidlauncher.ui.widgets.network.TerminalNetworkWidget
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

/**
 * NetworkWidget — Displays live upload & download speed, retaining SSID and IP.
 * Routes to user-selected styles: ELEGANT, MINIMAL, TERMINAL, RETRO.
 * Supports minimal corner usage indicator and navigates to the detailed usage tracking page.
 */
@Composable
fun NetworkWidget(
    style: NetworkStyle = NetworkStyle.ELEGANT,
    modifier: Modifier = Modifier,
    font: WidgetFont? = null,
    showUsage: Boolean? = null,
    previewInfo: NetworkInfoState? = null,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val effectiveFont = font ?: userManager.getNetworkFont()
    val effectiveShowUsage = showUsage ?: userManager.getShowNetworkUsageOnWidget()
    var networkInfo by remember { mutableStateOf(previewInfo ?: NetworkInfoState()) }

    LaunchedEffect(previewInfo) {
        if (previewInfo != null) {
            networkInfo = previewInfo
            return@LaunchedEffect
        }
        // Poll every 2 seconds for smooth live upload and download speeds
        while (true) {
            networkInfo = NetworkHelper.getNetworkInfo(context)
            delay(2.seconds)
        }
    }

    when (style) {
        NetworkStyle.ELEGANT  -> ElegantNetworkWidget(
            networkInfo = networkInfo,
            modifier = modifier,
            font = effectiveFont,
            showUsage = effectiveShowUsage,
            onTap = onTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        NetworkStyle.MINIMAL  -> MinimalNetworkWidget(
            networkInfo = networkInfo,
            modifier = modifier,
            font = effectiveFont,
            showUsage = effectiveShowUsage,
            onTap = onTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        NetworkStyle.TERMINAL -> TerminalNetworkWidget(
            networkInfo = networkInfo,
            modifier = modifier,
            font = effectiveFont,
            showUsage = effectiveShowUsage,
            onTap = onTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        NetworkStyle.RETRO    -> RetroNetworkWidget(
            networkInfo = networkInfo,
            modifier = modifier,
            font = effectiveFont,
            showUsage = effectiveShowUsage,
            onTap = onTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
    }
}
