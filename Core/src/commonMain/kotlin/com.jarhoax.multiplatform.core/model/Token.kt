package com.jarhoax.multiplatform.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Token(@SerialName("access_token") val token: String)
