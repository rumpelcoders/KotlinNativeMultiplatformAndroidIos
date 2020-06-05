package com.rumpel.mpp.statesonsteroids.android.ui.geofencing

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

interface AddAutomationEntryDialogListener {
    fun addEntry(entry: AutomationEntry)
    fun saveEntry(state: AutomationEntry)
    fun deleteEntry(id: UUID)
}

@Parcelize
data class AutomationEntry(
    val id: UUID,
    val automationAction: String,
    val automationData: AutomationData,
    val statusText: String = "",
    val statusEmoji: String = "",
    val statusExpiration: Long = 0
) : Parcelable

sealed class AutomationData : Parcelable {
    @Parcelize
    data class GpsAutomationData(val latitude: Double, val longitude: Double, val radius: Float) :
        AutomationData()

    @Parcelize
    data class WifiAutomationData(val ssid: Double) : AutomationData()
}
