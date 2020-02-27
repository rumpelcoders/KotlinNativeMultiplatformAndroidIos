package com.jarhoax.multiplatform.core

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.withCharset
import io.ktor.utils.io.charsets.Charsets
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

expect fun platformName(): String


fun createApplicationScreenMessage(): String {
    return "Kotlin Rocks on ${platformName()}"
}

internal expect val ApplicationDispatcher: CoroutineDispatcher


class SlackApi(val clientId: String, val clientSecret: String) {

    private val client = HttpClient {

    }

    private val scope = "users.profile:write"

    private val callback = "http://www.test.com"

    fun authorize(callback: (String) -> Unit) {
        val address =
            Url("https://slack.com/oauth/authorize?client_id=$clientId&scope=$scope&redirect_uri=$callback")
        GlobalScope.apply {
            launch(ApplicationDispatcher) {
                val result: String = client.get {
                    url(address.toString())
                }
                callback(result)
            }
        }
    }

    private var token: String? = null

    fun setState(
        state: String,
        callback: (String) -> Unit,
        emoji: String
    ) {
        val address = Url(slackApiBaseUrl + "/users.profile.set")

        GlobalScope.apply {
            launch(ApplicationDispatcher) {
                val result: String = client.post {
                    url(address.toString())
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                    body = """
{
    "profile": {
        "status_text": "$state",
        "status_emoji": "$emoji",
        "status_expiration": 0
    }
}"""
                }
                callback(result)
            }
        }
    }

    fun onRedirectCodeReceived(url: String) {
        val code = Url(url).parameters["code"]
        val address =
            Url("${slackApiBaseUrl}oauth.access?client_id=$clientId&scope=$scope&redirect_uri=$callback&client_secret=$clientSecret&code=$code")
        GlobalScope.apply {
            launch(ApplicationDispatcher) {
                val result: String = client.get {
                    url(address.toString())
                }
                token = result
            }
        }
    }
}

private const val slackApiBaseUrl: String = "https://slack.com/api/"