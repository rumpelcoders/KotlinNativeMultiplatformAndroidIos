package com.jarhoax.multiplatform.demo

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import com.jarhoax.multiplatform.core.SlackApi
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val slackApi = SlackApi("TODO","TODO")

        slackApi.setState("Am i a train?", {
            GlobalScope.apply {
                launch(Dispatchers.Main) {
                    Log.d("MainActivity",it)
                }
            }
        }, ":mountain_railway:")
        slackApi.authorize {
            GlobalScope.apply {
                launch(Dispatchers.Main) {
                    Log.d("MainActivity",it)
                    web_view.getSettings().setJavaScriptEnabled(true);
                    web_view.setWebViewClient(object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                            if (url.startsWith("http://www.test.com")) {
                                Log.d("MainActivity",url)
                                slackApi.onRedirectCodeReceived(url)
                            }
                            return false
                        }
                    })
                    web_view.loadDataWithBaseURL("", it, "text/html", "UTF-8", "")
                }
            }
        }
        setContentView(R.layout.activity_main)
    }
}
