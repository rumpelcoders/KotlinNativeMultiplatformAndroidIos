package com.rumpel.mpp.statesonsteroids.android.ui.home

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.rumpel.mpp.statesonsteroids.android.R
import com.rumpel.mpp.statesonsteroids.android.ui.geofencing.AddAutomationEntryDialogFragment
import com.rumpel.mpp.statesonsteroids.android.util.SlackStateClickListener
import com.rumpel.mpp.statesonsteroids.android.util.assetJsonString
import com.rumpel.mpp.statesonsteroids.core.*
import com.rumpel.mpp.statesonsteroids.core.model.SlackState
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val addEntryTag: String = "add_entry"

class HomeFragment : Fragment(),
    AddEntryDialogListener, SlackStateClickListener {

    private lateinit var slackApi: SlackApi
    private var loadingIndicator: ProgressDialog? = null
    private val slackStates: MutableList<SlackState> = mutableListOf()
    private lateinit var applicationContext: Context


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        root.fab.setOnClickListener {
            onAddButtonClicked()
        }

        FileManager.initialize(context)

        applicationContext = activity?.applicationContext!!
        slackApi = SlackApi(assetJsonString(applicationContext))

        val clearStateButton = root.findViewById<Button>(R.id.btn_state)
        clearStateButton.text = getString(R.string.clear_state)
        clearStateButton.setOnClickListener { onClearButtonPressed() }

        slackStates.addAll(loadStates())
        if (slackStates.isEmpty()) {
            slackStates.initDefaultStates()
        }

        root.list_view.adapter =
            SlackStateAdapter(
                applicationContext,
                slackStates,
                this
            )

        authorize(slackApi)
        return root
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun authorize(slackApi: SlackApi) {
        loadingIndicator =
            ProgressDialog.show(activity, "", getString(R.string.loading), true)
        slackApi.authorize { result ->
            GlobalScope.apply {
                launch(Dispatchers.Main) {
                    if (result.isAuthenticated) {
                        swapViews()
                        removeProgressDialog()
                    } else {
                        web_view.visibility = View.VISIBLE
                        web_view.settings.javaScriptEnabled = true;
                        web_view.webViewClient = createWebViewClient(slackApi)
                        web_view.loadDataWithBaseURL("", result.content, "text/html", "UTF-8", "")
                    }
                }
            }

        }
    }

    private fun createWebViewClient(slackApi: SlackApi) = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) = removeProgressDialog()

        override fun shouldOverrideUrlLoading(view: WebView, url: String) =
            if (url.startsWith(redirectUrl)) {
                swapViews()
                slackApi.onRedirectCodeReceived(url) {
                    Log.d(HomeFragment::class.java.simpleName, "Authenticated!")
                }
                true
            } else {
                false
            }
    }

    private fun removeProgressDialog() {
        loadingIndicator?.dismiss()
    }

    private fun swapViews() {
        web_view.visibility = View.GONE
    }

    private fun onAddButtonClicked() {
        val newFragment = AddEntryDialogFragment.newInstance(this)
        newFragment.show(activity?.supportFragmentManager!!, addEntryTag)
    }

    private fun onClearButtonPressed() {
        slackApi.clearState {
            log("IT: $it")
            getString(R.string.state_cleared).postAsToast()
        }
    }

    override fun addEntry(entry: SlackState) {
        slackStates.add(entry)
        (list_view.adapter as SlackStateAdapter).notifyDataSetChanged()
        saveStates(slackStates)
    }

    override fun saveEntry(state: SlackState) {
        val first = slackStates.first { it.statusText == state.statusText }
        val indexOf = slackStates.indexOf(first)
        slackStates.remove(first)
        slackStates.add(indexOf, state)
        (list_view.adapter as SlackStateAdapter).notifyDataSetChanged()
        saveStates(slackStates)
    }

    override fun deleteEntry(stateText: String) {
        val first = slackStates.first { it.statusText == stateText }
        slackStates.remove(first)
        (list_view.adapter as SlackStateAdapter).notifyDataSetChanged()
        saveStates(slackStates)
    }

    override fun onStateClicked(state: SlackState) = state.set()

    override fun onStateLongClicked(state: SlackState) {
        AddEntryDialogFragment.newInstance(this, state)
            .show(activity?.supportFragmentManager!!, addEntryTag)
    }

    private fun SlackState.set() {
        slackApi.setState(
            statusText,
            statusEmoji,
            statusExpiration.toInt() // TODO: actually we would need a different model here, because of min vs. unix time
        ) {
            log("IT: $it")
            if (it.statusText == "error") {
                getString(R.string.unable_to_set_state).postAsToast()
            } else {
                getString(R.string.state_set).postAsToast()
            }
        }
    }

    private fun String.postAsToast(length: Int = Toast.LENGTH_SHORT) {
        GlobalScope.apply {
            launch(Dispatchers.Main) {
                Toast.makeText(applicationContext, this@postAsToast, length).show()
            }
        }
    }

    private fun MutableList<SlackState>.initDefaultStates() {
        val defaultStates = listOf(
            SlackState(
                getString(R.string.sample_state_lunch),
                getString(R.string.sample_emoji_lunch),
                30
            ),
            SlackState(
                getString(R.string.sample_state_afk),
                getString(R.string.sample_emoji_afk),
                20
            )
        )

        addAll(defaultStates)
    }
}

private fun log(text: String) {
    Log.d(HomeFragment::class.java.simpleName, text)
}
