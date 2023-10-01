package com.pilot.noiapp.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NatsConfiguration(
    @Json(name = "url") val url: String,
    @Json(name = "accessToken") val accessToken: String,
    @Json(name = "stream") val stream: String,
    @Json(name = "app") val app: App? = null
)


@JsonClass(generateAdapter = true)
data class App(
    @Json(name = "cVersion") val cVersion: Int
)