/*
 * Copyright (C) 2026- Swapnil Ingle
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.codershubinc.nullvoidlauncher

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.codershubinc.nullvoidlauncher.data.UserManager
import com.codershubinc.nullvoidlauncher.ui.bluetooth.BluetoothHelper
import com.codershubinc.nullvoidlauncher.ui.homescreen.HomeScreen
import com.codershubinc.nullvoidlauncher.ui.theme.NullVoidLauncherTheme

class MainActivity : ComponentActivity() {

    private val userManager by lazy { UserManager(this) }
    private val bluetoothPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Handled */ }

    private val activityRecognitionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper.registerStepSensor(this)
            com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper.syncSteps(this)
        }
    }

    private val healthConnectLauncher = registerForActivityResult(
        androidx.health.connect.client.PermissionController.createRequestPermissionResultContract()
    ) {
        com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper.syncSteps(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        applySystemBarsVisibility()
        checkBluetoothPermission()
        checkStepsPermission()
        com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper.registerStepSensor(this)
        com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper.syncSteps(this)
        setContent {
            NullVoidLauncherTheme {
                HomeScreen()
            }
        }
    }

    private fun checkBluetoothPermission() {
        if (userManager.getShowBluetoothWidget() &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !BluetoothHelper.hasBluetoothPermission(this)
        ) {
            bluetoothPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
        }
    }

    private fun checkStepsPermission() {
        if (userManager.getShowStepsWidget()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                !com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper.hasActivityRecognitionPermission(this)
            ) {
                activityRecognitionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            } else {
                com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper.registerStepSensor(this)
                com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper.syncSteps(this)
            }

            // If Health Connect is available on the device, prompt to connect Google Fit / Health Connect once
            if (com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper.isHealthConnectAvailable(this) &&
                !userManager.getHealthConnectPromptDismissed()
            ) {
                lifecycleScope.launch {
                    val hasPerm = com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper.hasHealthConnectPermission(this@MainActivity)
                    if (!hasPerm) {
                        userManager.saveHealthConnectPromptDismissed(true)
                        healthConnectLauncher.launch(com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper.HEALTH_CONNECT_PERMISSIONS)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        applySystemBarsVisibility()
        if (userManager.getShowStepsWidget()) {
            com.codershubinc.nullvoidlauncher.ui.steps.StepsHelper.syncSteps(this)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            applySystemBarsVisibility()
        }
    }

    private fun applySystemBarsVisibility() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        if (userManager.getHideStatusBar()) {
            windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
        } else {
            windowInsetsController.show(WindowInsetsCompat.Type.statusBars())
        }
    }
}