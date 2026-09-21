package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    dayText: String? = null
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val effectiveStyle = style ?: userManager.getDayStyle()
    val effectiveFont = font ?: userManager.getDayFont()

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

    Box(modifier = modifier) {
        when (effectiveStyle) {
            DayStyle.ELEGANT   -> ElegantDay(currentDay, font = effectiveFont)
            DayStyle.RETRO     -> RetroDay(currentDay, font = effectiveFont)
            DayStyle.MINIMAL   -> MinimalDay(currentDay, font = effectiveFont)
            DayStyle.MODERN    -> ModernDay(currentDay, font = effectiveFont)
            DayStyle.BRUTALIST -> BrutalistDay(currentDay, font = effectiveFont)
        }
    }
}
