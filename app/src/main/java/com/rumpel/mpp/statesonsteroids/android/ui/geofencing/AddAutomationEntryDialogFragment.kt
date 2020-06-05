package com.rumpel.mpp.statesonsteroids.android.ui.geofencing

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.rumpel.mpp.statesonsteroids.android.R
import kotlinx.android.synthetic.main.dialog_add_automation.view.*
import kotlinx.android.synthetic.main.dialog_add_entry.view.state_duration
import kotlinx.android.synthetic.main.dialog_add_entry.view.state_emoji
import kotlinx.android.synthetic.main.dialog_add_entry.view.state_text
import java.util.*

private const val argumentKey = "entry"

/**
 * A simple [DialogFragment] subclass.
 * Use the [AddAutomationEntryDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddAutomationEntryDialogFragment : DialogFragment() {

    private val automationTypeChangedListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                automationType = null
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                parent?.let {
                    automationType = parent.getItemAtPosition(pos) as String
                }
            }
        }

    private var automationType: String? = null
    private lateinit var listener: AddAutomationEntryDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            val view = inflater.inflate(R.layout.dialog_add_automation, null)
            ArrayAdapter.createFromResource(
                context,
                R.array.automation_types,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                view.automation_type_spinner.adapter = adapter
            }
            view.automation_type_spinner.onItemSelectedListener = automationTypeChangedListener
            val entry = arguments?.getParcelable<AutomationEntry>(argumentKey)
            setViewValues(view, entry)
            buildButtons(entry, builder, view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun setViewValues(
        view: View,
        entry: AutomationEntry?
    ) {
        view.state_text.setText(entry?.statusText)
        view.state_emoji.setText(entry?.statusEmoji)
        view.state_duration.setText((entry?.statusExpiration ?: 0).toString())
    }

    private fun buildButtons(
        entry: AutomationEntry?,
        builder: AlertDialog.Builder,
        view: View
    ) {
        if (entry == null) {
            builder.setView(view)
                // Add action buttons
                .setPositiveButton(
                    R.string.add
                ) { _, _ ->
                    listener.addEntry(
                        buildEntry(UUID.randomUUID(), view)
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
                        buildEntry(entry.id, view)
                    )
                }
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
                    dialog?.cancel()
                }
                .setNeutralButton("Delete") { _, _ ->
                    listener.deleteEntry(entry.id)
                }
        }
    }

    private fun buildEntry(id: UUID, view: View): AutomationEntry {
        return AutomationEntry(
            id,
            "view.automation_action_spinner.se",
            AutomationData.GpsAutomationData(
                view.latitude.text.toString().toDouble(),
                view.longitude.text.toString().toDouble(),
                view.radius.text.toString().toFloat()
            ),
            view.state_text.text.toString().trim(),
            view.state_emoji.text.toString().trim(),
            view.state_duration.text.toString().toLongOrNull() ?: 0
        )
    }

    companion object {
        fun newInstance(
            listener: AddAutomationEntryDialogListener,
            entry: AutomationEntry? = null
        ): AddAutomationEntryDialogFragment {
            val addEntryDialogFragment =
                AddAutomationEntryDialogFragment()
            entry?.let {
                with(Bundle()) {
                    putParcelable(argumentKey, entry)
                    addEntryDialogFragment.arguments = this
                }
            }
            addEntryDialogFragment.listener = listener
            return addEntryDialogFragment
        }
    }
}
