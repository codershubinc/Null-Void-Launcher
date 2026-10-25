# Add project specific ProGuard rules here.
# Preserve enum values for reflective SharedPreferences lookups in UserManager
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Preserve model and data classes serialized with JSON
-keep class com.codershubinc.nullvoidlauncher.data.** { *; }
-keep class com.codershubinc.nullvoidlauncher.utils.UpdateInfo { *; }
-keep class com.codershubinc.nullvoidlauncher.utils.AppVersion { *; }

# Preserve OkHttp reflection & internal rules
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Preserve Health Connect client
-keep class androidx.health.connect.** { *; }
