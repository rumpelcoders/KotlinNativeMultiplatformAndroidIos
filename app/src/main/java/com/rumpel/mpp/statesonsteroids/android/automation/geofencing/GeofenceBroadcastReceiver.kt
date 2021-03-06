package com.rumpel.mpp.statesonsteroids.android.automation.geofencing

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.rumpel.mpp.statesonsteroids.android.MainActivity
import com.rumpel.mpp.statesonsteroids.android.R
import com.rumpel.mpp.statesonsteroids.android.automation.wifi.setSlackState
import com.rumpel.mpp.statesonsteroids.android.util.assetJsonString
import com.rumpel.mpp.statesonsteroids.core.SlackApi
import com.rumpel.mpp.statesonsteroids.core.loadAutomationEntries
import com.rumpel.mpp.statesonsteroids.core.model.AutomationData
import com.rumpel.mpp.statesonsteroids.core.model.AutomationEntry

private const val CHANNEL_ID: String = "test_channel"


class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        context?.let { ctx ->
            val action = when (geofenceTransition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> "entered"
                Geofence.GEOFENCE_TRANSITION_EXIT -> "exited"
                Geofence.GEOFENCE_TRANSITION_DWELL -> "dwell"
                else -> "unknown"
            }
            val actionItems =
                filterEntries(
                    loadAutomationEntries(),
                    action,
                    geofencingEvent.triggeringGeofences.first().requestId
                )
            val slackApi = SlackApi(assetJsonString(ctx))
            actionItems.lastOrNull()?.let {
                showNotification(
                    ctx,
                    it.automationAction,
                    "Geofence notified $action ",
                    2
                )
                slackApi.setSlackState(it)
            }
        }

    }
}

private fun filterEntries(
    entries: List<AutomationEntry>,
    automationAction: String,
    fenceId: String
): List<AutomationEntry> =
    entries.filter { it.automationData is AutomationData.GpsAutomationData }
        .filter { it.automationAction == automationAction }
        .filter { it.id == fenceId }

fun showNotification(context: Context, text: String?, title: String, id: Int) {
    val builder = createNotification(context, title, text)
    with(NotificationManagerCompat.from(context)) {
        // notificationId is a unique int for each notification that you must define
        notify(id, builder.build())
    }
}

fun createNotification(context: Context, title: String, text: String?): NotificationCompat.Builder {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(text)
        .setSmallIcon(R.drawable.exo_notification_small_icon)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    createNotificationChannel(
        context
    )
    return builder
}

private fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.channel_name)
        val descriptionText = context.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager = getSystemService(context, NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }
}

