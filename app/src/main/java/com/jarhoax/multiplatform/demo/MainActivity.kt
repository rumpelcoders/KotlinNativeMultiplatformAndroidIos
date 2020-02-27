package com.jarhoax.multiplatform.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.jarhoax.multiplatform.core.SlackApi
import com.jarhoax.multiplatform.core.redirectUrl
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val slackApi = SlackApi("18044401633.961752886881","d7ee42e29b36e3face61217c59266b2b")

        startOAuthFlow(slackApi)
    }

    private fun startOAuthFlow(slackApi: SlackApi) {
        web_view.visibility = View.VISIBLE
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith(redirectUrl)) {
                    slackApi.onRedirectCodeReceived(url) {
                        slackApi.setState("We are doing it!", ":cake:") {
                            GlobalScope.apply {
                                launch(Dispatchers.Main) {
                                    web_view.visibility = View.GONE
                                    Toast.makeText(
                                        this@MainActivity,
                                        it,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }

                        }
                    }
                    return true
                }
                return false
            }
        })

        slackApi.authorize {
            GlobalScope.apply {
                launch(Dispatchers.Main) {
                    Log.d("MainActivity", it)
                    web_view.loadDataWithBaseURL("", it, "text/html", "UTF-8", "")
                }
            }
        }
    }
}
