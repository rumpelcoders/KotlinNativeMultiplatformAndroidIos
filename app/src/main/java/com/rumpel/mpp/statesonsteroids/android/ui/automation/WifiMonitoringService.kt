package com.rumpel.mpp.statesonsteroids.android.ui.automation

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.IBinder
import com.rumpel.mpp.statesonsteroids.android.R
import com.rumpel.mpp.statesonsteroids.android.util.assetJsonString
import com.rumpel.mpp.statesonsteroids.core.SlackApi
import com.rumpel.mpp.statesonsteroids.core.loadAutomationEntries
import com.rumpel.mpp.statesonsteroids.core.model.AutomationData
import com.rumpel.mpp.statesonsteroids.core.model.AutomationEntry
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

private const val STATE_CONNECTED = "connected"
private const val STATE_DISCONNECTED = "disconnected"

class WifiMonitoringService : Service() {
    private var deviceName: String? = null
    private var networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network?) {
            val slackApi = SlackApi(assetJsonString(this@WifiMonitoringService))
            val entries = loadAutomationEntries()
            val actionItems = filterEntries(entries, STATE_DISCONNECTED, deviceName)
            actionItems.lastOrNull()?.let {
                slackApi.setSlackState(it)
            }
        }

        override fun onUnavailable() {
        }

        override fun onLosing(network: Network?, maxMsToLive: Int) {
        }

        override fun onAvailable(network: Network?) {
            val slackApi = SlackApi(assetJsonString(this@WifiMonitoringService))
            getDeviceName()

            val entries = loadAutomationEntries()
            val actionItems = filterEntries(entries, STATE_CONNECTED, deviceName)
            actionItems.lastOrNull()?.let {
                slackApi.setSlackState(it)
            }
        }


    }

    private fun getDeviceName() {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        deviceName = wifiManager.deviceName()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        val createNotification =
            createNotification(
                this,
                getString(R.string.service_title),
                getString(R.string.service_description)
            )
        startForeground(1, createNotification.build())
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}

/**
 * Fetches Name of Current Wi-fi Access Point
 *
 * Returns blank string if received "SSID <unknown ssid>" which you get when location is turned off
 */
fun WifiManager.deviceName(): String = connectionInfo.ssid.run {
    if (this.contains("<unknown ssid>")) "UNKNOWN" else this
}

fun SlackApi.setSlackState(item: AutomationEntry) {
    authorize {
        if (it.isAuthenticated) {
            //we are in a very unstable network situation here as wifi just did something.
            //let's wait a bit for it to settle down.
            runBlocking {
                delay(5000)
            }
            setState(
                item.statusText,
                item.statusEmoji,
                item.statusExpiration.toInt()
            ) { }
        }
    }
}

private fun filterEntries(
    entries: List<AutomationEntry>,
    automationAction: String,
    deviceName: String?
): List<AutomationEntry> =
    entries.filter { it.automationData is AutomationData.WifiAutomationData }
        .filter { it.automationAction == automationAction }
        .filter {
            (it.automationData as AutomationData.WifiAutomationData).ssid == deviceName?.replace(
                "\"",
                ""
            )
        }
