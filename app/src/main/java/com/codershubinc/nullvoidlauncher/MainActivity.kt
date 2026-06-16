package com.codershubinc.nullvoidlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.codershubinc.nullvoidlauncher.ui.home.HomeScreen
import com.codershubinc.nullvoidlauncher.ui.theme.NullVoidLauncherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NullVoidLauncherTheme {
                // Set the root to HomeScreen
                HomeScreen()
            }
        }
    }
}