package com.jarhoax.multiplatform.demo

import com.jarhoax.multiplatform.core.model.SlackState

interface AddEntryDialogListener {
  fun addEntry(entry: SlackState)
  fun saveEntry(state: SlackState)
  fun deleteEntry(stateText: String)
}
