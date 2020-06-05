package com.rumpel.mpp.statesonsteroids.core

import com.rumpel.mpp.statesonsteroids.core.model.AutomationData
import com.rumpel.mpp.statesonsteroids.core.model.AutomationEntry
import com.rumpel.mpp.statesonsteroids.core.model.SlackState
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

private const val stateFileName = "states.json"
private const val automationFileName = "automations.json"

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

fun saveAutomationEntries(entries: MutableList<AutomationEntry>) {
    val filePath =
        buildPath(automationFileName)
    val tokenJson = json().stringify(AutomationEntry.serializer().list, entries)
    filePath.let {
        FileManager.writeFile(it, tokenJson, true)
    }
}

fun loadAutomationEntries(): List<AutomationEntry> {
    val filePath = buildPath(automationFileName)
    if (FileManager.exists(filePath)) {
        FileManager.readFile(filePath, ContentEncoding.Utf8)?.let {
            json().parse(AutomationEntry.serializer().list, it).let { states ->
                return states
            }
        }
    }
    return listOf()
}

private fun json(): Json {
    val messageModule = SerializersModule { // 1
        polymorphic(AutomationData::class) { // 2
            AutomationData.GpsAutomationData::class with AutomationData.GpsAutomationData.serializer() // 3
            AutomationData.WifiAutomationData::class with AutomationData.WifiAutomationData.serializer() // 4
        }
    }
    return Json(context = messageModule)
}

private fun buildPath(fileName: String): PathComponent {
    return FileManager.contentsDirectory.absolutePath?.byAppending(fileName)!!
}
