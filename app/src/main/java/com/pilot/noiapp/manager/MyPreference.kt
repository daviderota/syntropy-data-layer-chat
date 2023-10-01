package com.pilot.noiapp.manager

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.pilot.noiapp.model.NatsConfiguration
import com.pilot.noiapp.utils.moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.jvm.internal.impl.renderer.KeywordStringsGenerated


@Singleton
class MyPreference @Inject constructor(@ApplicationContext val context: Context) {
    private val SHARED_USER_DATA = "SHARED_USER_DATA"
    private val APP_VERSION = "APP_VERSION"
    var url: String = ""
    var accessToken: String = ""
    var stream: String = ""



    private fun getUserData(): SharedPreferences {
        return context.getSharedPreferences(SHARED_USER_DATA, Context.MODE_PRIVATE)
    }
    fun setNatsConfiguration(accessToken: String, url: String, stream: String) {
        this.url = url
        this.accessToken = accessToken
        this.stream = stream

    }

    fun getNatsConfiguration(): NatsConfiguration {
        return NatsConfiguration(url, accessToken, stream)
    }

    fun getDeviceAppCVersion():Int {
        val editor: SharedPreferences.Editor = getUserData().edit()
        return get<Int>(APP_VERSION) ?: 1
    }



    private inline fun <reified T> get(key: String): T? {
        val value = getUserData().getString(key, null)
        if (value != null) {
            return moshi.adapter(T::class.java).fromJson(value)
        }
        return null
    }

    private fun <T> set(key: String, value: T) {
        val editor = getUserData().edit()
        if (value == null) {
            editor.putString(key, null)
        } else {
            when (value) {
                is String -> editor.putString(key, value as String)
                is Boolean -> editor.putBoolean(key, value as Boolean)
                is Long -> editor.putString(key, value.toString())
                is Int -> editor.putInt(key, value.toInt())
            }
        }

        editor.commit()

    }


    companion object {
        const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY"
        const val URL = "URL"
        const val STREAM_NAME = "STREAM_NAME"
    }
}