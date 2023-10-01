package com.pilot.noiapp.model

import android.content.Context
import com.pilot.noiapp.ui.MainActivity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DataLayerMessage(
    @Json(name = "uid") val uid: String? = null,
    @Json(name = "msg") val msg: String,
    val easyUid: String? = null
)
