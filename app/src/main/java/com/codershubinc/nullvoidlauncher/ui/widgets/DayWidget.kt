package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.runtime.*
import com.codershubinc.nullvoidlauncher.data.DayStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.day.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.minutes

@Composable
fun DayWidget(style: DayStyle) {
    var dayText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val dayFormatter = SimpleDateFormat("EEEE", Locale.getDefault())
        while (true) {
            dayText = dayFormatter.format(Date()).uppercase()
            delay(1.minutes)
        }
    }

    when (style) {
        DayStyle.MINIMAL -> MinimalDay(dayText)
        DayStyle.MODERN -> ModernDay(dayText)
        DayStyle.PIXEL -> PixelDay(dayText)
        DayStyle.ELEGANT -> ElegantDay(dayText)
        DayStyle.BRUTAL -> BrutalDay(dayText)
    }
}
