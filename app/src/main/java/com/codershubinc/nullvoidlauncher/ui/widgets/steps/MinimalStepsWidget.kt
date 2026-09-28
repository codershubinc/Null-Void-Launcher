package com.codershubinc.nullvoidlauncher.ui.widgets.steps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper
import com.codershubinc.nullvoidlauncher.ui.steps.StepsInfoState

/**
 * MinimalStepsWidget — Sleek inline minimal steps widget.
 */
@Composable
fun MinimalStepsWidget(
    stepsInfo: StepsInfoState,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.DEFAULT,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (onTap != null) onTap()
                        else if (onClick != null) onClick()
                        else StepsHelper.openGoogleFitOrHealth(context)
                    },
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongClick?.invoke()
                    }
                )
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.DirectionsWalk,
            contentDescription = null,
            tint = GoogleFitBlue.copy(alpha = 0.85f),
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "${stepsInfo.formattedSteps} steps",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = font.toFontFamily()
        )
        Text(
            text = "(${stepsInfo.progressPercent}%)",
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 11.sp,
            fontFamily = font.toFontFamily()
        )
    }
}
