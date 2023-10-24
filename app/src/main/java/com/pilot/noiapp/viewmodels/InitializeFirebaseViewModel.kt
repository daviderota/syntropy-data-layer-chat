package com.pilot.noiapp.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.pilot.noiapp.manager.MyPreference
import com.pilot.noiapp.model.NatsConfiguration
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class InitializeFirebaseViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
    private val sharedPreferencesRepository: MyPreference
) : ViewModel() {

    val completeEvent = MutableLiveData<Boolean>() // true if strings retrieved, false otherwise
    val cVersionError = MutableLiveData<Boolean>()

    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    enum class FirebaseRemoteConfigAction(val rawValue: String) {
        DATALAYER("dataLayer")
    }

    init {
        firebaseInitialize()
    }

    private fun firebaseInitialize() {
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val updatedConfig = firebaseRemoteConfig.all
                updatedConfig[FirebaseRemoteConfigAction.DATALAYER.rawValue]?.let { json ->
                    initializeNatsConfig(json.asString())
                } ?: run {
                    completeEvent.postValue(false)
                }

            } else {
                completeEvent.postValue(false)
            }
        }
    }


    private fun initializeNatsConfig(json: String) {
        val configuration = moshi.adapter(NatsConfiguration::class.java).fromJson(json)

        val cVersion = configuration?.app?.cVersion ?: 1
        if (sharedPreferencesRepository.getDeviceAppCVersion() < cVersion)
            cVersionError.postValue(true)
        else {
            sharedPreferencesRepository.setNatsConfiguration(
                "SAAMJD2U6M3XBKN66H7RWVMKXO3Q6K62SRWFXY7R2BIAJZV3FJD7V6HXZE",
                "nats://amberdm-sandbox-b1.syntropystack.com",
                "NoiAppStream"
            )
            completeEvent.postValue(true)
        }

    }

}