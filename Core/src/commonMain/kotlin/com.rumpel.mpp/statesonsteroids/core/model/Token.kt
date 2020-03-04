package com.rumpel.mpp.statesonsteroids.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Token(@SerialName("access_token") val token: String)
