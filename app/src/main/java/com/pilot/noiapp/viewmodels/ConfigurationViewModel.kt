package com.pilot.noiapp.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pilot.noiapp.model.NatsConfiguration
import com.pilot.noiapp.manager.MyPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val sharedPreferencesRepository: MyPreference,
) : ViewModel() {

    val configuration:MutableLiveData<NatsConfiguration> = MutableLiveData()

    fun setConfiguration(accessToken: String, url: String, token: String) {
        sharedPreferencesRepository.setNatsConfiguration(accessToken, url, token)
    }

    fun getConfiguration() {
        sharedPreferencesRepository.getNatsConfiguration()?.let{
            configuration.postValue(it)
        }
    }
}