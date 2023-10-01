package com.pilot.noiapp.service

import io.nats.client.Message

interface DataUpdateListener {
    fun onDataFromDataLayer(data: ByteArray) //from user
    fun connectionMessageHandler(status:Message) //from Nats connection system
    fun error(e:Exception)
    fun natsConnectionSuccess()
}