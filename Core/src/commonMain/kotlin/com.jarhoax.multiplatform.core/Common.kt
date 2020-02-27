package com.jarhoax.multiplatform.core

import com.soywiz.klock.DateTime
import com.soywiz.klock.TimeSpan
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
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

internal expect val ApplicationDispatcher: CoroutineDispatcher

class SlackApi(apiPropertiesString: String, private var token: String?) {

    @UnstableDefault
    private val apiProperties: ApiProperties =
        Json.plain.parse(ApiProperties.serializer(), apiPropertiesString)

    private val client = HttpClient()
    private val scope = "users.profile:write"

    fun authorize(callback: (String) -> Unit) {
        val address =
            Url(
                "https://slack.com/oauth/authorize?client_id=${apiProperties.clientId}" +
                        "&scope=$scope" +
                        "&redirect_uri=$redirectUrl"
            )
        GlobalScope.apply {
            launch(ApplicationDispatcher) {
                val result: String = client.get {
                    url(address.toString())
                }
                callback(result)
            }
        }
    }

    fun onRedirectCodeReceived(url: String, callback: () -> Unit) {
        val code = Url(url).parameters["code"]
        val address =
            Url(
                "${slackApiBaseUrl}oauth.access?client_id=${apiProperties.clientId}" +
                        "&scope=$scope" +
                        "&redirect_uri=$redirectUrl" +
                        "&client_secret=${apiProperties.clientSecret}" +
                        "&code=$code"
            )
        GlobalScope.apply {
            launch(ApplicationDispatcher) {
                val result: String = client.get {
                    url(address.toString())
                }
                //this is a hack (athon). Will probably break soon.s
                token = result.split(",")[1].split("\"")[3].trim('"')
                callback()
            }
        }
    }

    fun setState(
        state: String,
        emoji: String,
        duration: Int = 0,
        callback: (String) -> Unit
    ) {
        val address = Url("${slackApiBaseUrl}/users.profile.set")

        val expirationDateTime = if (duration <= 0) 0 else (DateTime.nowUnixLong() / 1000) + (duration * 60)

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
                                    "status_expiration": $expirationDateTime
                                }
                            }"""
                }
                callback(result)
            }
        }
    }
}

fun SlackApi.clearState(callback: (String) -> Unit){ this.setState("","", 0, callback)}

private const val slackApiBaseUrl: String = "https://slack.com/api/"
const val redirectUrl: String = "http://www.test.com"
