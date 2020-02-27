package com.jarhoax.multiplatform.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SlackState(
    @SerialName("status_text") val statusText: String = "",
    @SerialName("status_emoji") val statusEmoji: String = "",
    @SerialName("status_expiration") val statusExpiration: Long = 0
)
