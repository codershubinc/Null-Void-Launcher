package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.codershubinc.nullvoidlauncher.data.StorageStyle
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.widgets.storage.*
import com.codershubinc.nullvoidlauncher.utils.StorageInfoState
import com.codershubinc.nullvoidlauncher.utils.StorageUtils

/**
 * StorageWidget — Displays real-time device storage telemetry (used percentage, available, total).
 * Routes to user-selected styles: ELEGANT, MINIMAL, GAUGE_BAR, RING, TERMINAL, RETRO.
 * Interacts with system storage settings on click.
 */
@Composable
fun StorageWidget(
    modifier: Modifier = Modifier,
    style: StorageStyle? = null,
    font: WidgetFont? = null,
    previewInfo: StorageInfoState? = null,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val effectiveStyle = style ?: userManager.getStorageStyle()
    val effectiveFont = font ?: userManager.getStorageFont()
    val storageInfo = previewInfo ?: remember { StorageUtils.getStorageInfo(context) }

    when (effectiveStyle) {
        StorageStyle.ELEGANT   -> ElegantStorageWidget(
            storageInfo = storageInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = onTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        StorageStyle.MINIMAL   -> MinimalStorageWidget(
            storageInfo = storageInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = onTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        StorageStyle.GAUGE_BAR -> GaugeBarStorageWidget(
            storageInfo = storageInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = onTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        StorageStyle.RING      -> RingStorageWidget(
            storageInfo = storageInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = onTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        StorageStyle.TERMINAL  -> TerminalStorageWidget(
            storageInfo = storageInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = onTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
        StorageStyle.RETRO     -> RetroStorageWidget(
            storageInfo = storageInfo,
            modifier = modifier,
            font = effectiveFont,
            onTap = onTap,
            onLongClick = onLongClick,
            onClick = onClick
        )
    }
}
