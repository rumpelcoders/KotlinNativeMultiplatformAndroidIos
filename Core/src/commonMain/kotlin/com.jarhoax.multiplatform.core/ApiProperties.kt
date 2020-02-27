package com.jarhoax.multiplatform.core

import kotlinx.serialization.Serializable

@Serializable
data class ApiProperties(val clientId: String, val clientSecret: String)
