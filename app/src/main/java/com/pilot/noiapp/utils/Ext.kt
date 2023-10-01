package com.pilot.noiapp.utils

import android.content.Context
import com.pilot.noiapp.model.DataLayerMessage
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

@OptIn(ExperimentalStdlibApi::class)
fun DataLayerMessage.toJson(): String {
    val json = moshi.adapter<DataLayerMessage>().toJson(this)
    return json
}

@OptIn(ExperimentalStdlibApi::class)
fun String.toDataLayerMessage(): DataLayerMessage? {
    val result = moshi.adapter<DataLayerMessage>().fromJson(this)

    return result
}