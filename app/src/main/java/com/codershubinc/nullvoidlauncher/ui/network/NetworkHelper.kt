package com.codershubinc.nullvoidlauncher.ui.network

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.NetworkInterface
import java.util.Collections

data class NetworkInfoState(
    val isConnected: Boolean = false,
    val isWifi: Boolean = false,
    val isCellular: Boolean = false,
    val isEthernet: Boolean = false,
    val ssid: String? = null,
    val ipv4: String? = null,
    val ipv6: String? = null,
    val linkSpeedMbps: Int = 0,
    val frequencyMhz: Int = 0,
    val signalLevel: Int = 0, // 0 to 4
    val upSpeedBytesPerSec: Long = 0L,
    val downSpeedBytesPerSec: Long = 0L,
    val upSpeedText: String = "0 B/s",
    val downSpeedText: String = "0 B/s",
    val todayUsageBytes: Long = 0L,
    val todayUsageText: String = "0 B",
    val monthUsageBytes: Long = 0L,
    val monthUsageText: String = "0 B"
) {
    val connectionType: String
        get() = when {
            !isConnected -> "Offline"
            isWifi -> "Wi-Fi"
            isCellular -> "Cellular"
            isEthernet -> "Ethernet"
            else -> "Connected"
        }

    val displaySsid: String
        get() = when {
            !ssid.isNullOrBlank() &&
            ssid != "<unknown ssid>" &&
            ssid != "unknown ssid" &&
            ssid != "Wi-Fi Network" &&
            ssid != "Wi-Fi" -> ssid
            else -> ""
        }

    val hasDisplaySsid: Boolean
        get() = displaySsid.isNotBlank()

    val displayIp: String
        get() = ipv4 ?: ipv6 ?: "127.0.0.1"

    val displaySpeed: String
        get() = if (isConnected) "↑ $upSpeedText  ↓ $downSpeedText" else "Offline"

    val subtitleText: String
        get() = when {
            !isConnected -> "Offline"
            hasDisplaySsid -> "$displaySsid • $displayIp"
            else -> displayIp
        }
}

object NetworkHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    fun openWifiSettings(context: Context) {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val fallback = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try { context.startActivity(fallback) } catch (_: Exception) {}
        }
    }

    /**
     * Inspects current network capabilities, Wi-Fi attributes, and IP address.
     */
    fun getNetworkInfo(context: Context): NetworkInfoState {
        val speedSample = NetworkUsageTracker.sample(context)

        val cm = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return NetworkInfoState(
                todayUsageBytes = speedSample.todayUsageBytes,
                todayUsageText = speedSample.todayUsageText,
                monthUsageBytes = speedSample.monthUsageBytes,
                monthUsageText = speedSample.monthUsageText
            )

        val activeNetwork = cm.activeNetwork ?: return NetworkInfoState(
            isConnected = false,
            todayUsageBytes = speedSample.todayUsageBytes,
            todayUsageText = speedSample.todayUsageText,
            monthUsageBytes = speedSample.monthUsageBytes,
            monthUsageText = speedSample.monthUsageText
        )
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return NetworkInfoState(
            isConnected = false,
            todayUsageBytes = speedSample.todayUsageBytes,
            todayUsageText = speedSample.todayUsageText,
            monthUsageBytes = speedSample.monthUsageBytes,
            monthUsageText = speedSample.monthUsageText
        )

        val isConnected = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val isWifi = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val isCellular = caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        val isEthernet = caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)

        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

        // Extract WifiInfo if available
        val wifiInfo: WifiInfo? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            (caps.transportInfo as? WifiInfo) ?: wifiManager?.connectionInfo
        } else {
            wifiManager?.connectionInfo
        }

        var ssid: String? = null
        var linkSpeed = 0
        var freq = 0
        var signalLevel = 3

        if (isWifi && wifiInfo != null) {
            val raw = wifiInfo.ssid
            if (!raw.isNullOrBlank() && raw != "<unknown ssid>")
                ssid = raw.removeSurrounding("\"")
            linkSpeed = wifiInfo.linkSpeed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                freq = wifiInfo.frequency
            }
            val rssi = wifiInfo.rssi
            signalLevel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && wifiManager != null) {
                wifiManager.calculateSignalLevel(rssi)
            } else {
                @Suppress("DEPRECATION")
                WifiManager.calculateSignalLevel(rssi, 5)
            }
        }

        // Extract IP addresses
        val linkProperties = cm.getLinkProperties(activeNetwork)
        val addresses = linkProperties?.linkAddresses?.map { it.address } ?: emptyList()
        val ipv4FromLink = addresses.filterIsInstance<Inet4Address>().firstOrNull()?.hostAddress
        val ipv6FromLink = addresses.filterIsInstance<Inet6Address>().firstOrNull()?.hostAddress

        val resolvedIpv4 = ipv4FromLink ?: getFallbackIpv4(preferWifi = isWifi)

        return NetworkInfoState(
            isConnected = isConnected,
            isWifi = isWifi,
            isCellular = isCellular,
            isEthernet = isEthernet,
            ssid = ssid,
            ipv4 = resolvedIpv4,
            ipv6 = ipv6FromLink,
            linkSpeedMbps = linkSpeed,
            frequencyMhz = freq,
            signalLevel = signalLevel,
            upSpeedBytesPerSec = speedSample.upSpeedBytesPerSec,
            downSpeedBytesPerSec = speedSample.downSpeedBytesPerSec,
            upSpeedText = speedSample.upSpeedText,
            downSpeedText = speedSample.downSpeedText,
            todayUsageBytes = speedSample.todayUsageBytes,
            todayUsageText = speedSample.todayUsageText,
            monthUsageBytes = speedSample.monthUsageBytes,
            monthUsageText = speedSample.monthUsageText
        )
    }

    /**
     * Emits live updates whenever connection states, capabilities, or link properties change.
     */
    fun observeNetworkInfo(context: Context): Flow<NetworkInfoState> = callbackFlow {
        val appContext = context.applicationContext
        val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Emit initial value
        trySend(getNetworkInfo(appContext))

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(getNetworkInfo(appContext))
            }

            override fun onLost(network: Network) {
                trySend(getNetworkInfo(appContext))
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                trySend(getNetworkInfo(appContext))
            }

            override fun onLinkPropertiesChanged(network: Network, linkProperties: android.net.LinkProperties) {
                trySend(getNetworkInfo(appContext))
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        cm.registerNetworkCallback(request, callback)

        awaitClose {
            try {
                cm.unregisterNetworkCallback(callback)
            } catch (_: Exception) {}
        }
    }.flowOn(Dispatchers.IO)

    private fun getFallbackIpv4(preferWifi: Boolean): String? {
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            // If wifi, prefer interface starting with wlan
            val sorted = if (preferWifi) {
                interfaces.sortedByDescending { it.name.startsWith("wlan") }
            } else interfaces

            for (intf in sorted) {
                if (intf.isUp && !intf.isLoopback) {
                    val addrs = Collections.list(intf.inetAddresses)
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress && addr is Inet4Address) {
                            val host = addr.hostAddress
                            if (!host.isNullOrBlank() && !host.startsWith("127.")) {
                                return host
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) {}
        return null
    }
}
