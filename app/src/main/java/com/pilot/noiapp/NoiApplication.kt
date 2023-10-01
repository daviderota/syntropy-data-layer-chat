package com.pilot.noiapp

import android.app.Application
import com.pilot.network.NatsProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NoiApplication:Application() {

    override fun onCreate() {
        super.onCreate()
    }

}