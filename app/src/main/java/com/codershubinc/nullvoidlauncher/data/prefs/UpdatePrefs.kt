package com.codershubinc.nullvoidlauncher.data.prefs

import android.content.SharedPreferences

class UpdatePrefs(prefs: SharedPreferences) : BasePrefs(prefs) {
    var autoCheckUpdates: Boolean by booleanPref("auto_check_updates", true)
    var lastUpdateCheckTime: Long by longPref("last_update_check_time", 0L)
    var updatePostponedUntil: Long by longPref("update_postponed_until", 0L)
    var skippedVersion: String by stringPref("skipped_update_version", "")
    var networkTriggerUrl: String by stringPref("network_trigger_url", "")
    var networkTriggerPayload: String by stringPref("network_trigger_payload", "")
}
