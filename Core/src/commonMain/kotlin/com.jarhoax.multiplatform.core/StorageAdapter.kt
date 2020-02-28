package com.jarhoax.multiplatform.core

import com.jarhoax.multiplatform.core.model.SlackState
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

private const val stateFileName = "states.json"

fun saveStates(slackStates: MutableList<SlackState>){
    val filePath =
        buildPath(stateFileName)
    val tokenJson = Json.stringify(SlackState.serializer().list, slackStates)
    filePath.let {
        FileManager.writeFile(it, tokenJson, true)
    }
}

fun loadStates() : List<SlackState> {
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

private fun buildPath(fileName: String): PathComponent {
    return FileManager.contentsDirectory.absolutePath?.byAppending(fileName)!!
}
