package com.rumpel.mpp.statesonsteroids.android.ui.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent =
            GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {
            context?.let {
                Toast.makeText(
                    context,
                    "geofence says: " + geofencingEvent.triggeringGeofences.first().requestId,
                    Toast.LENGTH_SHORT
                ).show()
            }


        } else {
            // Log the error.
        }
    }
}
