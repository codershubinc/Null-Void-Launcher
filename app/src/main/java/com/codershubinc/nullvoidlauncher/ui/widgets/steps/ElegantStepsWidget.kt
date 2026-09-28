package com.codershubinc.nullvoidlauncher.ui.widgets.steps

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsWalk
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.data.WidgetFont
import com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper
import com.codershubinc.nullvoidlauncher.ui.steps.StepsInfoState

// Google Fit primary brand colors
val GoogleFitBlue = Color(0xFF4285F4)
val GoogleFitGreen = Color(0xFF34A853)
val GoogleFitYellow = Color(0xFFFBBC05)
val GoogleFitRed = Color(0xFFEA4335)

/**
 * ElegantStepsWidget — Google Fit aesthetic with frosted glassmorphism,
 * activity progress ring, step count, and calories/distance metrics.
 */
@Composable
fun ElegantStepsWidget(
    stepsInfo: StepsInfoState,
    modifier: Modifier = Modifier,
    font: WidgetFont = WidgetFont.DEFAULT,
    onTap: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val userManager = remember { UserManager(context) }
    val cornerRadius = userManager.getWidgetCornerRadius()
    val glassEffect = userManager.getWidgetGlassEffect()
    val shape = RoundedCornerShape(cornerRadius.dp)

    val progress = stepsInfo.progress

    Row(
        modifier = modifier
            .wrapContentWidth()
            .clip(shape)
            .background(
                if (glassEffect) {
                    Brush.horizontalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.White.copy(alpha = 0.03f)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        listOf(Color(0xFF141414), Color(0xFF101010))
                    )
                }
            )
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        GoogleFitBlue.copy(alpha = 0.35f),
                        GoogleFitGreen.copy(alpha = 0.20f)
                    )
                ),
                shape
            )
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
            }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Mini Progress Ring Icon
        Box(
            modifier = Modifier.size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 2.5.dp.toPx()
                // Track
                drawArc(
                    color = Color.White.copy(alpha = 0.12f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth)
                )
                // Fit Gradient Progress
                drawArc(
                    brush = Brush.sweepGradient(listOf(GoogleFitBlue, GoogleFitGreen, GoogleFitBlue)),
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.DirectionsWalk,
                contentDescription = "Steps",
                tint = if (progress >= 1f) GoogleFitGreen else GoogleFitBlue,
                modifier = Modifier.size(13.dp)
            )
        }

        // Steps Count and Goal
        Column {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                        append(stepsInfo.formattedSteps)
                    }
                    withStyle(style = SpanStyle(color = Color.White.copy(alpha = 0.45f), fontWeight = FontWeight.Normal)) {
                        append(" / ${stepsInfo.formattedGoal} steps")
                    }
                },
                fontSize = 12.sp,
                fontFamily = font.toFontFamily()
            )

            Text(
                text = "${stepsInfo.formattedDistance} • ${stepsInfo.formattedCalories}",
                color = Color.White.copy(alpha = 0.45f),
                fontSize = 10.sp,
                fontFamily = font.toFontFamily()
            )
        }
    }
}
