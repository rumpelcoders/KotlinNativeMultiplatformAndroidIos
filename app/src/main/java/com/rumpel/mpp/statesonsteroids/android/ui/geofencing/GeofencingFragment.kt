package com.rumpel.mpp.statesonsteroids.android.ui.geofencing

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rumpel.mpp.statesonsteroids.android.R
import com.rumpel.mpp.statesonsteroids.core.loadAutomationEntries
import com.rumpel.mpp.statesonsteroids.core.model.AutomationEntry
import com.rumpel.mpp.statesonsteroids.core.saveAutomationEntries
import kotlinx.android.synthetic.main.fragment_geofencing.*
import kotlinx.android.synthetic.main.fragment_geofencing.view.*
import kotlinx.android.synthetic.main.fragment_geofencing.view.list_view


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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_geofencing, container, false)
        root.fab.setOnClickListener {
            onAddButtonClicked()
        }
        loadAutomationEntries().forEach {
            entries.add(it)
        }
        activity?.let {
            checkGeofencingPermission(it)
        }

        context?.startForegroundService(Intent(context, WifiMonitoringService::class.java))
        //todo load all geofences

        root.list_view.adapter =
            AutomationEntryAdapter(
                context!!,
                entries,
                automationEntryClickListener
            )


        return root
    }

    private fun onAddButtonClicked() {
        val newFragment = AddAutomationEntryDialogFragment.newInstance(this)
        newFragment.show(activity?.supportFragmentManager!!, "addAutomationEntryTag")
    }



    override fun addEntry(entry: AutomationEntry) {
        entries.add(entry)
        (list_view.adapter as AutomationEntryAdapter).notifyDataSetChanged()
        saveAutomationEntries(entries)
    }

    override fun updateEntry(entry: AutomationEntry) {
        val first = entries.first { it.id == entry.id }
        entries.remove(first)
        entries.add(entry)
        (list_view.adapter as AutomationEntryAdapter).notifyDataSetChanged()
        saveAutomationEntries(entries)
    }

    override fun deleteEntry(id: String) {
        val first = entries.first { it.id == id }
        entries.remove(first)
        (list_view.adapter as AutomationEntryAdapter).notifyDataSetChanged()
        saveAutomationEntries(entries)
    }
}



