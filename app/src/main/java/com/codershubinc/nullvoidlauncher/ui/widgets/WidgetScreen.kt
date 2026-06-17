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
            }
    ) {
        when (clockStyle) {
            ClockStyle.VERTICAL -> {
                // Rotated Layout: Clock Top-Left, Music Centered
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    Box(modifier = Modifier
                        .padding(top = 120.dp, start = 0.dp)
                        .align(Alignment.TopStart)
                    ) {
                        ClockWidget(clockStyle)
                    }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        MusicWidget()
                    }
                }
            }
            ClockStyle.MODERN -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    Box(modifier = Modifier
                        .padding(top = 140.dp, start = 0.dp)
                    ) {
                        ClockWidget(clockStyle)
                    }
                    
                    FavoritesWidget()
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    MusicWidget()
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            else -> {
                // Standard Centered Layout
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ClockWidget(clockStyle)
                    Spacer(modifier = Modifier.height(24.dp))
                    MusicWidget()
                }
            }
        }
    }
}
