package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.codershubinc.nullvoidlauncher.data.DayStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.day.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration.Companion.minutes

@Composable
fun DayWidget(modifier: Modifier = Modifier, style: DayStyle) {
    var dayText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val dayFormatter = SimpleDateFormat("EEEE", Locale.getDefault())
        while (true) {
            dayText = dayFormatter.format(Date()).uppercase()
            delay(1.minutes)
        }
    }

    Box(modifier = modifier) {
        when (style) {
            DayStyle.MINIMAL -> MinimalDay(dayText)
            DayStyle.MODERN -> ModernDay(dayText)
            DayStyle.PIXEL -> PixelDay(dayText)
            DayStyle.ELEGANT -> ElegantDay(dayText)
            DayStyle.BRUTAL -> BrutalDay(dayText)
        }
    }
}
