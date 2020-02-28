package com.jarhoax.multiplatform.demo

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import com.jarhoax.multiplatform.core.model.SlackState
import kotlinx.android.synthetic.main.dialog_add_entry.*
import kotlinx.android.synthetic.main.dialog_add_entry.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [AddEntryDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddEntryDialogFragment : DialogFragment() {

    private lateinit var listener: AddEntryDialogListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            val view = inflater.inflate(R.layout.dialog_add_entry, null)
            builder.setView(view)
                // Add action buttons
                .setPositiveButton(
                    R.string.add
                ) { _, _ ->
                    listener.addEntry(
                        SlackState(
                            view.state_text.text.toString().trim(),
                            view.state_emoji.text.toString().trim(),
                            view.state_duration.text.toString().toLongOrNull() ?: 0
                        )
                    )
                }
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as AddEntryDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() +
                        " must implement NoticeDialogListener")
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AddEntryDialogFragment()
    }
}
