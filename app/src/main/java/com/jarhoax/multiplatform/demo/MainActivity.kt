package com.jarhoax.multiplatform.demo

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import android.widget.Toast
import com.jarhoax.multiplatform.core.FileManager
import com.jarhoax.multiplatform.core.SlackApi
import com.jarhoax.multiplatform.core.model.SlackState
import com.jarhoax.multiplatform.core.redirectUrl
import com.jarhoax.multiplatform.demo.util.assetJsonString
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), AddEntryDialogListener {

    lateinit var slackApi: SlackApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FileManager.initialize(applicationContext)

        val apiProperties = assetJsonString(applicationContext)
        slackApi = SlackApi(apiProperties)

        list_view.adapter = ArrayAdapter<SlackState>(this,R.layout.dialog_add_entry)

        authorize(slackApi)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun authorize(slackApi: SlackApi) {
        web_view.visibility = View.VISIBLE
        val pd = ProgressDialog.show(this@MainActivity, "", getString(R.string.loading),true);
        slackApi.authorize { result ->
            GlobalScope.apply {
                launch(Dispatchers.Main) {
                    if (result == "ok") {
                        swapViews()
                    } else {
                        web_view.settings.javaScriptEnabled = true;
                        web_view.webViewClient = object : WebViewClient() {
                            override fun onPageFinished(
                                view: WebView?,
                                url: String?
                            ) {
                                if (pd != null && pd.isShowing) {
                                    pd.dismiss()
                                }
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

    private fun swapViews() {
        web_view.visibility = View.GONE
        main_text.text = "You are authenticated 🍺"
    }

    fun onLunchButtonClicked(view: View) {
        // TODO: add state handling
        slackApi.setState("@ lunch", ":knife_fork_plate:", 5) {
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

    fun onAfkButtonClicked(view: View) {
        // TODO: add state handling
        slackApi.setState("AFK", ":keyboard:", 1) {
            GlobalScope.apply {
                launch(Dispatchers.Main) {
                    Toast
                        .makeText(this@MainActivity, "State set successfully!", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    fun onAddButtonClicked(view: View) {
        val newFragment = AddEntryDialogFragment.newInstance()
        newFragment.show(supportFragmentManager, "add_entry")
    }

    override fun addEntry(entry: SlackState) {
        //todo add entry
    }
}
