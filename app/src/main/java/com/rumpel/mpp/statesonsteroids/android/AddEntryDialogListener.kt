package com.rumpel.mpp.statesonsteroids.android

import com.rumpel.mpp.statesonsteroids.core.model.SlackState

interface AddEntryDialogListener {
  fun addEntry(entry: SlackState)
  fun saveEntry(state: SlackState)
  fun deleteEntry(stateText: String)
}
