package com.rumpel.mpp.statesonsteroids.android.ui.geofencing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import com.rumpel.mpp.statesonsteroids.android.R
import com.rumpel.mpp.statesonsteroids.core.model.SlackState
import com.rumpel.mpp.statesonsteroids.android.util.SlackStateClickListener

class AutomationEntryAdapter(
    context: Context,
    objects: MutableList<AutomationEntry>,
    private val clickListener: AutomationEntryClickListener
) :
    ArrayAdapter<AutomationEntry>(context,
        R.layout.item_slack_state, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView

        if (view == null) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_slack_state, parent, false)
            val stateViewHolder =
                StateViewHolder(
                    view,
                    clickListener
                )
            view.tag = stateViewHolder
        }

        val holder = view!!.tag as StateViewHolder
        val state = getItem(position) ?: return view

        val packageName: String = context.packageName
        val resId: Int = context.resources.getIdentifier(
            state.statusEmoji.replace(":", ""),
            "string",
            packageName
        )

        val emoji = try {
            context.getString(resId)
        } catch (e: Exception) {
            state.statusEmoji
        }
        holder.button.text = "${state.statusText} $emoji ${state.statusExpiration}min"
        holder.button.setOnClickListener { holder.clickListener.onEntryClicked(state) }
        holder.button.setOnLongClickListener {
            holder.clickListener.onEntryLongClicked(state)
            return@setOnLongClickListener true

        }


        return view
    }

    class StateViewHolder(val itemView: View, val clickListener: AutomationEntryClickListener) {
        val button: Button = itemView.findViewById(R.id.btn_state)
    }
}

interface AutomationEntryClickListener {
    fun onEntryClicked(entry: AutomationEntry)
    fun onEntryLongClicked(entry: AutomationEntry)
}
