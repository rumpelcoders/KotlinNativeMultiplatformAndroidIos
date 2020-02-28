package com.jarhoax.multiplatform.demo

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.jarhoax.multiplatform.core.FileManager
import com.jarhoax.multiplatform.core.SlackApi
import com.jarhoax.multiplatform.core.model.SlackState
import com.jarhoax.multiplatform.core.redirectUrl
import com.jarhoax.multiplatform.demo.util.SlackStateClickListener
import com.jarhoax.multiplatform.demo.util.assetJsonString
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), AddEntryDialogListener, SlackStateClickListener {

    lateinit var slackApi: SlackApi
    private var loadingIndicator: ProgressDialog? = null
    private val slackStates = mutableListOf<SlackState>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FileManager.initialize(applicationContext)

        val apiProperties = assetJsonString(applicationContext)
        slackApi = SlackApi(apiProperties)

        initDefaultStates()
        list_view.adapter = SlackStateAdapter(this, slackStates, this)

        authorize(slackApi)
    }

    private fun initDefaultStates() {
        val defaultStates = listOf(
            SlackState("@ lunch", ":knife_fork_plate:", 30),
            SlackState("AFK", ":coffee:", 20)
        )

        slackStates.addAll(defaultStates)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun authorize(slackApi: SlackApi) {
        loadingIndicator =
            ProgressDialog.show(this@MainActivity, "", getString(R.string.loading), true);
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

    override fun addEntry(entry: SlackState) {
        slackStates.add(entry)
        (list_view.adapter as SlackStateAdapter).notifyDataSetChanged()
    }

    override fun onStateClicked(state: SlackState) {
        setState(state)
    }

    private fun setState(slackState: SlackState) {
        slackApi.setState(
            slackState.statusText, slackState.statusEmoji,
            slackState.statusExpiration.toInt() // actually we would need a different model here, because of min vs. unix time
        ) {
            GlobalScope.apply {
                launch(Dispatchers.Main) {
                    Log.d(MainActivity::class.java.simpleName, "IT: $it")
                    Toast
                        .makeText(this@MainActivity, "State set successfully!", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}
