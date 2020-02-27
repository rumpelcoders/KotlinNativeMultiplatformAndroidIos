package com.jarhoax.multiplatform.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.jarhoax.multiplatform.core.SlackApi
import com.jarhoax.multiplatform.core.redirectUrl
import com.jarhoax.multiplatform.demo.util.assetJsonString
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apiProperties = assetJsonString(applicationContext)
        val token: String? = null // TODO load token from file
        val slackApi = SlackApi(apiProperties, token)

        if (token.isNullOrEmpty()) {
            authorize(slackApi)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun authorize(slackApi: SlackApi) {
        slackApi.authorize {
            GlobalScope.apply {
                launch(Dispatchers.Main) {
                    web_view.settings.javaScriptEnabled = true;
                    web_view.webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
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

                    web_view.loadDataWithBaseURL("", it, "text/html", "UTF-8", "")
                }
            }
        }
    }

    private fun swapViews() {
        web_view.visibility = View.GONE
        main_text.text = "You are authenticated 🍺"
    }
}
