package com.rumpel.mpp.statesonsteroids.android.ui.geofencing

import com.rumpel.mpp.statesonsteroids.core.model.AutomationEntry

import java.util.*

interface AddAutomationEntryDialogListener {
    fun addEntry(entry: AutomationEntry)
    fun updateEntry(state: AutomationEntry)
    fun deleteEntry(id: String)
}

