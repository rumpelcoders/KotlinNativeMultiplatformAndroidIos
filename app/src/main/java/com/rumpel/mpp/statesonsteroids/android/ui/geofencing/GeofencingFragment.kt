package com.rumpel.mpp.statesonsteroids.android.ui.geofencing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.rumpel.mpp.statesonsteroids.android.R

class GeofencingFragment : Fragment() {

    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofencingViewModel: GeofencingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        geofencingViewModel =
            ViewModelProviders.of(this).get(GeofencingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_geofencing, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        geofencingViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        geofencingClient = LocationServices.getGeofencingClient(activity!!)
        return root
    }
}
