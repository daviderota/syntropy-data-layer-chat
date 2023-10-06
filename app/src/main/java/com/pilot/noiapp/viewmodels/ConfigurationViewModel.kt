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
    val username:MutableLiveData<String?> = MutableLiveData()

    init{
        loadUsername()
    }

    private fun loadUsername(){
        username.postValue(sharedPreferencesRepository.getUsername())
    }

    fun setConfiguration(accessToken: String, url: String, token: String) {
        sharedPreferencesRepository.setNatsConfiguration(accessToken, url, token)
    }

    fun setUsername(username:String){
        sharedPreferencesRepository.setUsername(username)
        this.username.postValue(username)
    }

    fun getConfiguration() {
        sharedPreferencesRepository.getNatsConfiguration()?.let{
            configuration.postValue(it)
        }
    }
}