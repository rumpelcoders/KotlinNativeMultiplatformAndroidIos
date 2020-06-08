package com.rumpel.mpp.statesonsteroids.android.ui.automatization

import com.rumpel.mpp.statesonsteroids.core.model.AutomationEntry

interface AddAutomationEntryDialogListener {
    fun addEntry(entry: AutomationEntry)
    fun updateEntry(state: AutomationEntry)
    fun deleteEntry(id: String)
}

