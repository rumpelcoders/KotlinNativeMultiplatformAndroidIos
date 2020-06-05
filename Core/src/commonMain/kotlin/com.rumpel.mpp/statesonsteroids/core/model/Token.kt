package com.rumpel.mpp.statesonsteroids.core.model

import com.rumpel.mpp.statesonsteroids.core.Parcelable
import com.rumpel.mpp.statesonsteroids.core.Parcelize
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Token(@SerialName("access_token") val token: String)

@Serializable
@Parcelize
data class AutomationEntry(
    val id: String,
    val automationAction: String,
    @Polymorphic
    val automationData: AutomationData,
    val statusText: String = "",
    val statusEmoji: String = "",
    val statusExpiration: Long = 0
): Parcelable

@Serializable
@Parcelize
open class AutomationData : Parcelable {
    @Serializable
    data class GpsAutomationData(val latitude: Double, val longitude: Double, val radius: Float) :
        AutomationData()
    @Serializable
    data class WifiAutomationData(val ssid: String) : AutomationData()
}
