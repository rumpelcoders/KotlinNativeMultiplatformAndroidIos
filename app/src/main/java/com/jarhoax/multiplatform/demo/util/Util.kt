package com.jarhoax.multiplatform.demo.util

import android.content.Context
import android.util.Log
import java.io.IOException

fun assetJsonString(context: Context): String? {
    val json: String
    try {
        val inputStream = context.assets.open("properties.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.use { it.read(buffer) }
        json = String(buffer)
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    // print the data
    Log.i("data", json)
    return json
}

