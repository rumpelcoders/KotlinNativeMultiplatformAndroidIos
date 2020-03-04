package com.rumpel.mpp.statesonsteroids.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiProperties(val clientId: String, val clientSecret: String)


