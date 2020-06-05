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
import com.rumpel.mpp.statesonsteroids.core.saveString
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import java.util.*


class GeofencingFragment : Fragment(),
    AddAutomationEntryDialogListener {

    private val automationEntryClickListener = object : AutomationEntryClickListener {
        override fun onEntryClicked(entry: AutomationEntry) {
            val newFragment = AddAutomationEntryDialogFragment.newInstance(this@GeofencingFragment, entry)
            newFragment.show(activity?.supportFragmentManager!!, "addAutomationEntryTag")
        }

        override fun onEntryLongClicked(entry: AutomationEntry) {
            TODO("Not yet implemented")
        }

    }
    private val entries = mutableListOf<AutomationEntry>()
    private val geofenceList = mutableListOf<Geofence>()
    lateinit var geofencingClient: GeofencingClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_geofencing, container, false)
        root.fab.setOnClickListener {
            onAddButtonClicked()
        }

        root.list_view.adapter =
            AutomationEntryAdapter(
                context!!,
                entries,
                automationEntryClickListener
            )

        geofencingClient = LocationServices.getGeofencingClient(activity!!)

        geofenceList.add(
            createGeoFence(
                GeoFenceData(
                    UUID.randomUUID().toString(),
                    "S3 Incubator",
                    46.8328899,
                    12.7611502,
                    40.0F
                )
            )
        )
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
                val lastLocation = LocationServices.getFusedLocationProviderClient(activity!!)
                lastLocation.lastLocation.addOnSuccessListener(activity!!) { location ->
                    if (location != null) {
                        val wayLatitude = location.latitude
                        val wayLongitude = location.longitude

                        showNotification(
                            context!!,
                            "latitude: $wayLatitude longitude: $wayLongitude",
                            "Geofence was added. Current location is:",
                            1
                        )
                    }
                }
            }
            addOnFailureListener {
                Toast.makeText(context!!, "geofence failed to add", Toast.LENGTH_SHORT).show()
            }
        }
        return root
    }

    private fun onAddButtonClicked() {
        val newFragment = AddAutomationEntryDialogFragment.newInstance(this)
        newFragment.show(activity?.supportFragmentManager!!, "addAutomationEntryTag")
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

    override fun addEntry(entry: AutomationEntry) {
        entries.add(entry)
        val data = entries.joinToString { it.toString() }
        saveString(data, "entries")
        (list_view.adapter as AutomationEntryAdapter).notifyDataSetChanged()
    }

    override fun updateEntry(entry: AutomationEntry) {
        TODO("Not yet implemented")
        val first = entries.first { it.id == entry.id }
        entries.remove(first)
        entries.add(entry)
        (list_view.adapter as AutomationEntryAdapter).notifyDataSetChanged()
    }

    override fun deleteEntry(id: UUID) {
        val first = entries.first { it.id == id }
        entries.remove(first)
        (list_view.adapter as AutomationEntryAdapter).notifyDataSetChanged()
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
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .setTransitionTypes(
            Geofence.GEOFENCE_TRANSITION_EXIT
                    or Geofence.GEOFENCE_TRANSITION_ENTER
        )
        .build()
}

