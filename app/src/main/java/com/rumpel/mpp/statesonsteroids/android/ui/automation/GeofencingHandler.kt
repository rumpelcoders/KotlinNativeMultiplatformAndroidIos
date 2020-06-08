package com.rumpel.mpp.statesonsteroids.android.ui.automation

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofencingHandler(private val context: Context) {
    private var geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
    private val geofenceList = mutableListOf<Geofence>()


    fun add(geoFenceData: GeoFenceData) {
        geofenceList.add(
            createGeoFence(
                geoFenceData
            )
        )
    }

    fun startObserving() {
        if (ActivityCompat.checkSelfPermission(
                context.applicationContext!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if(geofenceList.isEmpty()){
            return
        }
        geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent)?.run {
            addOnSuccessListener {
                Toast.makeText(context, "geofence added", Toast.LENGTH_SHORT).show()
            }
            addOnFailureListener {
                Toast.makeText(context, "geofence failed to add", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createGeoFence(entry: GeoFenceData): Geofence {
        return Geofence.Builder()
            .setRequestId(entry.key)
            .setCircularRegion(
                entry.latitude,
                entry.longitude,
                entry.radius
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_EXIT
                        or Geofence.GEOFENCE_TRANSITION_ENTER
            )
            .build()
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}

fun checkGeofencingPermission(activity: Activity) {
    if (ActivityCompat.checkSelfPermission(
            activity.applicationContext!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        )
    }
}
