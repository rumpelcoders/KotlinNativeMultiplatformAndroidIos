@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.rumpel.mpp.statesonsteroids.android

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import com.rumpel.mpp.statesonsteroids.android.util.SlackStateClickListener
import com.rumpel.mpp.statesonsteroids.android.util.assetJsonString
import com.rumpel.mpp.statesonsteroids.core.*
import com.rumpel.mpp.statesonsteroids.core.model.SlackState
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),
    AddEntryDialogListener, SlackStateClickListener {

    private lateinit var slackApi: SlackApi
    private var loadingIndicator: ProgressDialog? = null
    private val slackStates = mutableListOf<SlackState>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FileManager.initialize(applicationContext)

        slackApi = SlackApi(assetJsonString(applicationContext))

        val clearStateButton = btn_clear_state.findViewById<Button>(R.id.btn_state)
        clearStateButton.text = getString(R.string.clear_state)
        clearStateButton.setOnClickListener { onClearButtonPressed() }

        slackStates.addAll(loadStates())
        if (slackStates.isEmpty()) {
            slackStates.initDefaultStates()
        }

        list_view.adapter =
            SlackStateAdapter(
                this,
                slackStates,
                this
            )

        authorize(slackApi)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun authorize(slackApi: SlackApi) {
        loadingIndicator = ProgressDialog.show(this, "", getString(R.string.loading), true)
        slackApi.authorize { result ->
            GlobalScope.apply {
                launch(Dispatchers.Main) {
                    if (result == "ok") {
                        swapViews()
                        removeProgressDialog()
                    } else {
                        web_view.visibility = View.VISIBLE
                        web_view.settings.javaScriptEnabled = true;
                        web_view.webViewClient = object : WebViewClient() {
                            override fun onPageFinished(
                                view: WebView?,
                                url: String?
                            ) {
                                removeProgressDialog()
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                url: String
                            ): Boolean {
                                if (url.startsWith(redirectUrl)) {
                                    swapViews()

                                    slackApi.onRedirectCodeReceived(url) {
                                        Log.d(MainActivity::class.java.simpleName, "Authenticated!")
                                    }

                                    return true
                                }
                                return false
                            }
                        }

                        web_view.loadDataWithBaseURL("", result, "text/html", "UTF-8", "")
                    }
                }
            }

        }
    }

    private fun removeProgressDialog() {
        loadingIndicator?.dismiss()
    }

    private fun swapViews() {
        web_view.visibility = View.GONE
    }

    fun onAddButtonClicked(view: View) {
        val newFragment = AddEntryDialogFragment.newInstance()
        newFragment.show(supportFragmentManager, "add_entry")
    }

    private fun onClearButtonPressed() {
        slackApi.clearState {
            Log.d(MainActivity::class.java.simpleName, "IT: $it")
            postToast("State cleared!")
        }
    }

    override fun addEntry(entry: SlackState) {
        slackStates.add(entry)
        (list_view.adapter as SlackStateAdapter).notifyDataSetChanged()
        saveStates(slackStates)

    }

    override fun saveEntry(state: SlackState) {
        val first = slackStates.first { it -> it.statusText == state.statusText }
        val indexOf = slackStates.indexOf(first)
        slackStates.remove(first)
        slackStates.add(indexOf, state)
        (list_view.adapter as SlackStateAdapter).notifyDataSetChanged()
        saveStates(slackStates)
    }

    override fun deleteEntry(stateText: String) {
        val first = slackStates.first { it -> it.statusText == stateText }
        val indexOf = slackStates.indexOf(first)
        slackStates.remove(first)
        (list_view.adapter as SlackStateAdapter).notifyDataSetChanged()
        saveStates(slackStates)
    }

    override fun onStateClicked(state: SlackState) {
        setState(state)
    }

    override fun onStateLongClicked(state: SlackState) {
        val newFragment = AddEntryDialogFragment.newInstance(state)
        newFragment.show(supportFragmentManager, "add_entry")
    }

    private fun setState(slackState: SlackState) {
        slackApi.setState(
            slackState.statusText, slackState.statusEmoji,
            slackState.statusExpiration.toInt() // TODO: actually we would need a different model here, because of min vs. unix time
        ) {
            Log.d(MainActivity::class.java.simpleName, "IT: $it")
            if (it.statusText == "error") {
                postToast("Unable to set state. Maybe the emoji does not exist?")
            } else {
                postToast("State set successfully!")
            }
        }
    }

    private fun postToast(text: String) {
        GlobalScope.apply {
            launch(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
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


