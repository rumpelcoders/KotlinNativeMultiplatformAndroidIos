package com.jarhoax.multiplatform.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    @SerialName("profile") val profile: SlackState
)