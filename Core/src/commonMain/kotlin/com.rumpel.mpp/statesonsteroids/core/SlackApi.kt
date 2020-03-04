package com.rumpel.mpp.statesonsteroids.core

import com.rumpel.mpp.statesonsteroids.core.model.ApiProperties
import com.rumpel.mpp.statesonsteroids.core.model.Profile
import com.rumpel.mpp.statesonsteroids.core.model.SlackState
import com.rumpel.mpp.statesonsteroids.core.model.Token
import com.soywiz.klock.DateTime
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json

private const val slackApiBaseUrl: String = "https://slack.com/api/"
const val redirectUrl: String = "http://www.test.com"

@UnstableDefault
class SlackApi(apiPropertiesString: String) {

    @UnstableDefault
    private val apiProperties: ApiProperties =
        Json.plain.parse(ApiProperties.serializer(), apiPropertiesString)

    private val client = HttpClient()
    private val scope = "users.profile:write"
    private var token: Token? = null
    private val tokenFilePath =
        FileManager.contentsDirectory.absolutePath?.byAppending("token.json")!!

    fun authorize(callback: (String) -> Unit) {
        if (FileManager.exists(tokenFilePath)) {
            FileManager.readFile(tokenFilePath, ContentEncoding.Utf8)?.let {
                Json.plain.parse(Token.serializer(), it).let { token ->
                    this.token = token
                    callback("ok")
                    return@authorize
                }
            }
        }

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

                token = Json.nonstrict.parse(Token.serializer(), result)
                val tokenJson = Json.stringify(Token.serializer(), token!!)

                tokenFilePath.let {
                    FileManager.writeFile(it, tokenJson, true)
                }

                callback()
            }
        }
    }

    fun readState(callback: (SlackState) -> Unit) {
        val address = Url("${slackApiBaseUrl}/users.profile.get")
        GlobalScope.apply {
            launch(ApplicationDispatcher) {
                val result: String = client.get {
                    url(address.toString())
                    header("Authorization", "Bearer ${token?.token}")
                }
                val profile = Json.nonstrict.parse(Profile.serializer(), result)
                callback(profile.state)
            }
        }
    }

    fun setState(
        state: String,
        emoji: String,
        duration: Int = 0,
        callback: (SlackState) -> Unit
    ) {
        val address = Url("${slackApiBaseUrl}/users.profile.set")

        val expirationDateTime =
            if (duration <= 0) 0 else (DateTime.nowUnixLong() / 1000) + (duration * 60)

        val b = Json.stringify(
            Profile.serializer(),
            Profile(
                SlackState(
                    state,
                    emoji,
                    expirationDateTime
                )
            )
        )
        GlobalScope.apply {
            launch(ApplicationDispatcher) {
                val result: String = client.post {
                    url(address.toString())
                    header("Authorization", "Bearer ${token?.token}")
                    contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                    body = b
                }
                try {
                    val profile = Json.nonstrict.parse(Profile.serializer(), result)
                    callback(profile.state)
                } catch (e: Exception) {
                    callback(SlackState("error"))
                }
            }
        }
    }
}

fun SlackApi.clearState(callback: (SlackState) -> Unit) {
    this.setState("", "", 0, callback)
}
