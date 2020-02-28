package com.jarhoax.multiplatform.demo

import com.jarhoax.multiplatform.core.model.SlackState

interface AddEntryDialogListener {
  fun addEntry(entry: SlackState)
}
