package com.rumpel.mpp.statesonsteroids.android

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.rumpel.mpp.statesonsteroids.core.model.SlackState
import kotlinx.android.synthetic.main.dialog_add_entry.view.*


private const val statusText = "statusText"

private const val statusEmoji = "statusEmoji"

private const val statusExpiration = "statusExpiration"

/**
 * A simple [Fragment] subclass.
 * Use the [AddEntryDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddEntryDialogFragment : DialogFragment() {

    private lateinit var listener: AddEntryDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            val view = inflater.inflate(R.layout.dialog_add_entry, null)

            val passedStatusText = arguments?.getString(statusText) ?: ""
            view.state_text.setText(passedStatusText)
            view.state_emoji.setText(arguments?.getString(statusEmoji) ?: "")
            view.state_duration.setText((arguments?.getLong(statusExpiration) ?: 0).toString())
            val isNewState = passedStatusText.isBlank()
            if (isNewState) {
                builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(
                        R.string.add
                    ) { _, _ ->
                        listener.addEntry(
                            buildState(view)
                        )
                    }
                    .setNegativeButton(
                        R.string.cancel
                    ) { _, _ ->
                        dialog?.cancel()
                    }
            } else {
                view.state_text.isEnabled = false
                builder.setView(view)
                    // Add action buttons
                    .setPositiveButton(
                        "Save"
                    ) { _, _ ->
                        listener.saveEntry(
                            buildState(view)
                        )
                    }
                    .setNegativeButton(
                        R.string.cancel
                    ) { _, _ ->
                        dialog?.cancel()
                    }
                    .setNeutralButton("Delete") { _, _ ->
                        listener.deleteEntry(
                            view.state_text.text.toString().trim()
                        )
                    }
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun buildState(view: View): SlackState {
        return SlackState(
            view.state_text.text.toString().trim(),
            view.state_emoji.text.toString().trim(),
            view.state_duration.text.toString().toLongOrNull() ?: 0
        )
    }

    companion object {
        fun newInstance(listener: AddEntryDialogListener, state: SlackState? = null): AddEntryDialogFragment {
            val addEntryDialogFragment =
                AddEntryDialogFragment()
            val args = Bundle()
            addEntryDialogFragment.listener = listener
            state?.let {
                args.putString(statusText, state.statusText)
                args.putString(statusEmoji, state.statusEmoji)
                args.putLong(statusExpiration, state.statusExpiration)
                addEntryDialogFragment.arguments = args
            }

            return addEntryDialogFragment
        }
    }
}
