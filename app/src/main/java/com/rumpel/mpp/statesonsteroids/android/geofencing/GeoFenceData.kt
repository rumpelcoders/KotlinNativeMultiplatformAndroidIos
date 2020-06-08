package com.rumpel.mpp.statesonsteroids.android.geofencing

data class GeoFenceData(
    val key: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float
)
