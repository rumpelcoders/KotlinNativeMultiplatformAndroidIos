package com.rumpel.mpp.statesonsteroids.android.ui.automation

import com.rumpel.mpp.statesonsteroids.core.model.AutomationEntry

interface AutomationEntryClickListener {
    fun onEntryClicked(entry: AutomationEntry)
    fun onEntryLongClicked(entry: AutomationEntry)
}
