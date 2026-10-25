package com.codershubinc.nullvoidlauncher.data.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class StringPrefDelegate(
    private val prefs: SharedPreferences,
    private val key: String,
    private val default: String
) : ReadWriteProperty<Any?, String> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return prefs.getString(key, default) ?: default
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        prefs.edit { putString(key, value) }
    }
}

class NullableStringPrefDelegate(
    private val prefs: SharedPreferences,
    private val key: String,
    private val default: String? = null
) : ReadWriteProperty<Any?, String?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): String? {
        return prefs.getString(key, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        prefs.edit {
            if (value == null) remove(key) else putString(key, value)
        }
    }
}

class BooleanPrefDelegate(
    private val prefs: SharedPreferences,
    private val key: String,
    private val default: Boolean
) : ReadWriteProperty<Any?, Boolean> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return prefs.getBoolean(key, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }
}

class IntPrefDelegate(
    private val prefs: SharedPreferences,
    private val key: String,
    private val default: Int
) : ReadWriteProperty<Any?, Int> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return prefs.getInt(key, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        prefs.edit { putInt(key, value) }
    }
}

class LongPrefDelegate(
    private val prefs: SharedPreferences,
    private val key: String,
    private val default: Long
) : ReadWriteProperty<Any?, Long> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Long {
        return prefs.getLong(key, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
        prefs.edit { putLong(key, value) }
    }
}

class FloatPrefDelegate(
    private val prefs: SharedPreferences,
    private val key: String,
    private val default: Float
) : ReadWriteProperty<Any?, Float> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Float {
        return prefs.getFloat(key, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
        prefs.edit { putFloat(key, value) }
    }
}

class EnumPrefDelegate<T : Enum<T>>(
    private val prefs: SharedPreferences,
    private val key: String,
    private val default: T,
    private val enumClass: Class<T>
) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val name = prefs.getString(key, default.name) ?: return default
        return try {
            java.lang.Enum.valueOf(enumClass, name)
        } catch (_: Exception) {
            default
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        prefs.edit { putString(key, value.name) }
    }
}

class StringSetPrefDelegate(
    private val prefs: SharedPreferences,
    private val key: String,
    private val default: Set<String>
) : ReadWriteProperty<Any?, Set<String>> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Set<String> {
        return prefs.getStringSet(key, default) ?: default
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Set<String>) {
        prefs.edit { putStringSet(key, value) }
    }
}

class IntListPrefDelegate(
    private val prefs: SharedPreferences,
    private val key: String,
    private val default: List<Int>
) : ReadWriteProperty<Any?, List<Int>> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): List<Int> {
        val set = prefs.getStringSet(key, null) ?: return default
        return set.mapNotNull { it.toIntOrNull() }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: List<Int>) {
        prefs.edit { putStringSet(key, value.map { it.toString() }.toSet()) }
    }
}

// ── Extension Factory Functions on SharedPreferences ─────────────────────────
fun SharedPreferences.stringPref(key: String, default: String) =
    StringPrefDelegate(this, key, default)

fun SharedPreferences.nullableStringPref(key: String, default: String? = null) =
    NullableStringPrefDelegate(this, key, default)

fun SharedPreferences.booleanPref(key: String, default: Boolean) =
    BooleanPrefDelegate(this, key, default)

fun SharedPreferences.intPref(key: String, default: Int) =
    IntPrefDelegate(this, key, default)

fun SharedPreferences.longPref(key: String, default: Long) =
    LongPrefDelegate(this, key, default)

fun SharedPreferences.floatPref(key: String, default: Float) =
    FloatPrefDelegate(this, key, default)

inline fun <reified T : Enum<T>> SharedPreferences.enumPref(key: String, default: T) =
    EnumPrefDelegate(this, key, default, T::class.java)

fun SharedPreferences.stringSetPref(key: String, default: Set<String> = emptySet()) =
    StringSetPrefDelegate(this, key, default)

fun SharedPreferences.intListPref(key: String, default: List<Int> = emptyList()) =
    IntListPrefDelegate(this, key, default)

// ── Base Prefs Class with Direct Helpers ─────────────────────────────────────
open class BasePrefs(val prefs: SharedPreferences) {
    protected fun stringPref(key: String, default: String) = prefs.stringPref(key, default)
    protected fun nullableStringPref(key: String, default: String? = null) = prefs.nullableStringPref(key, default)
    protected fun booleanPref(key: String, default: Boolean) = prefs.booleanPref(key, default)
    protected fun intPref(key: String, default: Int) = prefs.intPref(key, default)
    protected fun longPref(key: String, default: Long) = prefs.longPref(key, default)
    protected fun floatPref(key: String, default: Float) = prefs.floatPref(key, default)
    protected inline fun <reified T : Enum<T>> enumPref(key: String, default: T) = prefs.enumPref(key, default)
    protected fun stringSetPref(key: String, default: Set<String> = emptySet()) = prefs.stringSetPref(key, default)
    protected fun intListPref(key: String, default: List<Int> = emptyList()) = prefs.intListPref(key, default)
}
