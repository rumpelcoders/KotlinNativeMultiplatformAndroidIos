package com.jarhoax.multiplatform.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import com.jarhoax.multiplatform.core.SlackApi
import com.jarhoax.multiplatform.demo.util.assetJsonString
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiProperties = this.loadProperties()
        val slackApi = SlackApi(apiProperties.clientId, apiProperties.clientSecret)

        slackApi.authorize {
            GlobalScope.apply {
                launch(Dispatchers.Main) {
                    Log.d("MainActivity", it)
                    web_view.getSettings().setJavaScriptEnabled(true);
                    web_view.setWebViewClient(object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                            if (url.startsWith("http://www.com.jarhoax.multiplatform.core.test.com")) {
                                Log.d("MainActivity", url)
                                slackApi.onRedirectCodeReceived(url) {
                                    Log.d(MainActivity::class.java.simpleName, "Authenticated!")
                                }
                            }
                            return false
                        }
                    })
                    web_view.loadDataWithBaseURL("", it, "text/html", "UTF-8", "")
                }
            }
        }
    }

    private fun loadProperties(): ApiProperties {
        val jsonProperties: String? = assetJsonString(applicationContext)
        // TODO: parse actual properties and pass to slack api
        return ApiProperties("TODO", "TODO")
    }
}
