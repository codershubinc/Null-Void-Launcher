package com.codershubinc.nullvoidlauncher.ui.widgets

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.codershubinc.nullvoidlauncher.data.ClockStyle
import com.codershubinc.nullvoidlauncher.ui.widgets.bottombar.PixelBottomBar

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
                        MusicWidget(clockStyle)
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
                    
                    MusicWidget(clockStyle)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            ClockStyle.PIXEL -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    Box(modifier = Modifier
                        .padding(top = 160.dp)
                        .align(Alignment.TopStart)
                    ) {
                        ClockWidget(clockStyle)
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PixelBottomBar(onOpenDrawer = onOpenDrawer)
                    }
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
                    MusicWidget(clockStyle)
                }
            }
        }
    }
}
