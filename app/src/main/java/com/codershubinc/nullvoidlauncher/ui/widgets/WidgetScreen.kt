package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.codershubinc.nullvoidlauncher.data.ClockStyle

@Composable
fun WidgetScreen(isDrawerOpen: Boolean, clockStyle: ClockStyle, onOpenDrawer: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(isDrawerOpen) {
                if (!isDrawerOpen) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        if (dragAmount < -20) {
                            onOpenDrawer()
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ClockWidget(clockStyle)
            Spacer(modifier = Modifier.height(24.dp))
            MusicWidget()
        }
    }
}
