package com.rumpel.mpp.statesonsteroids.android.util

import android.content.Context
import com.rumpel.mpp.statesonsteroids.core.model.SlackState
import java.io.IOException

fun assetJsonString(context: Context): String {
    val json: String
    try {
        val inputStream = context.assets.open("properties.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.use { it.read(buffer) }
        json = String(buffer)
    } catch (ioException: IOException) {
        throw IllegalStateException("Properties file missing")
    }

    return json
}

interface SlackStateClickListener {
    fun onStateClicked(state: SlackState)
    fun onStateLongClicked(state: SlackState)
}

