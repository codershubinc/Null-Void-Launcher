package com.codershubinc.nullvoidlauncher.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen() {
    var isDrawerOpen by remember { mutableStateOf(false) }

    // Intercept the system Back button to close the drawer
    BackHandler(enabled = isDrawerOpen) {
        isDrawerOpen = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // --- 1. Background Layer (Clock & Swipe Up Area) ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(isDrawerOpen) {
                    // Only listen for swipes if the drawer is closed
                    if (!isDrawerOpen) {
                        detectVerticalDragGestures { change, dragAmount ->
                            change.consume()
                            if (dragAmount < -20) { // Swipe UP
                                isDrawerOpen = true
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            ClockWidget()
        }

        // --- 2. The App Drawer Overlay ---
        AnimatedVisibility(
            visible = isDrawerOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            AppDrawerScreen(
                onClose = { isDrawerOpen = false }
            )
        }
    }
}

@Composable
fun ClockWidget() {
    var timeText by remember { mutableStateOf("") }

    // Update the time every second
    LaunchedEffect(Unit) {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        while (true) {
            timeText = formatter.format(Date())
            delay(1000)
        }
    }

    Text(
        text = timeText,
        color = Color.White,
        fontSize = 72.sp,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 4.sp
    )
}