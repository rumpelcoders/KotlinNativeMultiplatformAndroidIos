package com.jarhoax.multiplatform.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    @SerialName("profile") val state: SlackState
)
