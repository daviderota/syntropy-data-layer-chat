package com.pilot.noiapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pilot.noiapp.model.DataLayerMessage

class SharedViewModel : ViewModel() {

    private val _messages = MutableLiveData<ArrayList<DataLayerMessage>?>()
    val messages:LiveData<ArrayList<DataLayerMessage>?> = _messages

    // function to send message
    fun sendMessage(data: DataLayerMessage?) {
        synchronized(_messages){
            data?.let {
                var oldData = if(_messages.value !=null) _messages.value else arrayListOf<ArrayList<DataLayerMessage>>()
                (oldData as ArrayList<DataLayerMessage>?)?.add(it)
                _messages.postValue(oldData)
            }
        }
    }


}