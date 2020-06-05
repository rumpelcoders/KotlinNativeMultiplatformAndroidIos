package com.rumpel.mpp.statesonsteroids.core

import com.rumpel.mpp.statesonsteroids.core.model.SlackState
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.typeOf

private const val stateFileName = "states.json"

fun saveStates(slackStates: MutableList<SlackState>) {
    val filePath =
        buildPath(stateFileName)
    val tokenJson = Json.stringify(SlackState.serializer().list, slackStates)
    filePath.let {
        FileManager.writeFile(it, tokenJson, true)
    }
}

fun loadStates(): List<SlackState> {
    val filePath =
        buildPath(stateFileName)
    if (FileManager.exists(filePath)) {
        FileManager.readFile(filePath, ContentEncoding.Utf8)?.let {
            Json.plain.parse(SlackState.serializer().list, it).let { states ->
                return states
            }
        }
    }
    return listOf()
}

inline fun saveString(data: String, identifier: String) {
    val filePath = FileManager.contentsDirectory.absolutePath?.byAppending(identifier)!!
    filePath.let {
        FileManager.writeFile(it, data, true)
    }
}

private fun buildPath(fileName: String): PathComponent {
    return FileManager.contentsDirectory.absolutePath?.byAppending(fileName)!!
}
