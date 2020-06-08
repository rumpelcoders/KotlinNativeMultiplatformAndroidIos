package com.rumpel.mpp.statesonsteroids.android.ui.automation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import com.rumpel.mpp.statesonsteroids.android.R
import com.rumpel.mpp.statesonsteroids.core.model.AutomationData
import com.rumpel.mpp.statesonsteroids.core.model.AutomationEntry

class AutomationEntryAdapter(
    context: Context,
    objects: MutableList<AutomationEntry>,
    private val clickListener: AutomationEntryClickListener
) :
    ArrayAdapter<AutomationEntry>(context, R.layout.item_slack_state, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_slack_state, parent, false)
            val stateViewHolder = StateViewHolder(view, clickListener)
            view.tag = stateViewHolder
        }

        val holder = view!!.tag as StateViewHolder
        val state = getItem(position) ?: return view


        val type = when (state.automationData) {
            is AutomationData.GpsAutomationData -> "GPS"
            is AutomationData.WifiAutomationData -> "WIFI"
            else -> "UNKNOWN"
        }

        val stateString = state.getStateString()

        holder.button.text = "$type(${state.automationAction}) $stateString"
        holder.button.setOnClickListener { holder.clickListener.onEntryClicked(state) }
        holder.button.setOnLongClickListener {
            holder.clickListener.onEntryLongClicked(state)
            return@setOnLongClickListener true
        }


        return view
    }

    private fun AutomationEntry.getStateString(): String {
        val packageName: String = context.packageName
        val resId: Int = context.resources.getIdentifier(
            statusEmoji.replace(":", ""),
            "string",
            packageName
        )
        val emoji = try {
            context.getString(resId)
        } catch (e: Exception) {
            statusEmoji
        }
        return if (statusText.isBlank() && emoji.isBlank()) {
            "clear"
        } else {
            " $statusText $emoji"
        }
    }

    class StateViewHolder(itemView: View, val clickListener: AutomationEntryClickListener) {
        val button: Button = itemView.findViewById(R.id.btn_state)
    }
}

