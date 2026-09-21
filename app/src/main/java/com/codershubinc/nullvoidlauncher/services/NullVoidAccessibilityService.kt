package com.codershubinc.nullvoidlauncher.services

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent

/**
 * NullVoidAccessibilityService — Background accessibility bridge providing:
 * 1. Double-tap to lock screen without disabling biometrics (fingerprint/face unlock).
 * 2. Swipe-down gesture to expand notification shade or quick settings.
 */
class NullVoidAccessibilityService : AccessibilityService() {

    companion object {
        var instance: NullVoidAccessibilityService? = null
            private set

        fun isServiceRunning(): Boolean = instance != null

        fun isServiceEnabled(context: Context): Boolean {
            if (instance != null) return true
            val expectedComponentName = ComponentName(context, NullVoidAccessibilityService::class.java).flattenToString()
            val enabledServicesSetting = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false

            val colonSplitter = TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServicesSetting)
            while (colonSplitter.hasNext()) {
                val componentNameString = colonSplitter.next()
                if (componentNameString.equals(expectedComponentName, ignoreCase = true)) {
                    return true
                }
            }
            return false
        }

        fun lockScreen(): Boolean {
            val service = instance ?: return false
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                service.performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
            } else {
                false
            }
        }

        fun expandNotifications(): Boolean {
            val service = instance ?: return false
            return service.performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
        }

        fun expandQuickSettings(): Boolean {
            val service = instance ?: return false
            return service.performGlobalAction(GLOBAL_ACTION_QUICK_SETTINGS)
        }

        fun openAccessibilitySettings(context: Context) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) {
            instance = null
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Passive service: only handles global actions triggered by launcher gestures
    }

    override fun onInterrupt() {}
}
