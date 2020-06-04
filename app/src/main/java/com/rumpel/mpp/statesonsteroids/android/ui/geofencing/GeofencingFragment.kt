package com.rumpel.mpp.statesonsteroids.android.ui.geofencing

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.rumpel.mpp.statesonsteroids.android.R

class GeofencingFragment : Fragment() {

    private val geofenceList = mutableListOf<Geofence>()
    lateinit var geofencingClient: GeofencingClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_geofencing, container, false)
        geofencingClient = LocationServices.getGeofencingClient(activity!!)

        geofenceList.add(createGeoFence(GeoFenceData("test", 46.832864, 12.761061, 100.0F)))
        if (ActivityCompat.checkSelfPermission(
                activity?.applicationContext!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
        geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent)?.run {
            addOnSuccessListener {
                Toast.makeText(context!!, "geofence added", Toast.LENGTH_SHORT).show()
            }
            addOnFailureListener {
                Toast.makeText(context!!, "geofence failed to add", Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
            addGeofences(geofenceList)
        }.build()
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}


fun createGeoFence(entry: GeoFenceData): Geofence {
    return Geofence.Builder()
        .setRequestId(entry.key)
        .setCircularRegion(
            entry.latitude,
            entry.longitude,
            entry.radius
        )
        .setExpirationDuration(30*60*1000) //30 minutes
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL)
        .build()
}

data class GeoFenceData(
    val key: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float
)
