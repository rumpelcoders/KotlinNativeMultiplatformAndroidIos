package com.jarhoax.multiplatform.demo.util

import android.content.Context
import com.jarhoax.multiplatform.core.model.SlackState
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
}

