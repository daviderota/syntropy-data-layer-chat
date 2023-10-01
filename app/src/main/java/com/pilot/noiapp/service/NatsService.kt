package com.pilot.noiapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.pilot.network.NatsProvider
import com.pilot.noiapp.R
import com.pilot.noiapp.manager.MyPreference
import com.pilot.noiapp.ui.MainActivity
import io.nats.client.Message
import io.nats.client.MessageHandler
import io.nats.client.Nats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread


class NatsService : Service() {

    private val CHANNEL_ID = "NatsForegroundServiceChannel"
    private var dataUpdateListener: DataUpdateListener? = null
    private var connectJob: Job? = null
    private var natsProvider: NatsProvider? = null

    companion object {
        const val SEND_TO_DATA_LAYER = "SEND_TO_DATA_LAYER"
        const val CHAT_MESSAGE = "CHAT_MESSAGE"
    }

    private val binder: IBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        val service: NatsService = this@NatsService
    }

    private val serviceBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            if (action != null && action.contains(SEND_TO_DATA_LAYER)) {
                val chatMsg = intent.getStringExtra(CHAT_MESSAGE)
                natsProvider?.let {
                    chatMsg?.let { chatMsg ->
                        it.publish(chatMsg)
                    } ?: run {
                        dataUpdateListener?.error(Exception())
                    }
                } ?: run {
                    dataUpdateListener?.error(Exception())
                }
            }
            // Log.d("MyService", "onReceive number = $number")
        }
    }

    override fun onCreate() {
        super.onCreate()
        val intentFilter = IntentFilter(NatsService.SEND_TO_DATA_LAYER)
        registerReceiver(serviceBroadcastReceiver, intentFilter)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val natsUrl = intent.getStringExtra(MyPreference.URL) ?: ""
        val accessToken = intent.getStringExtra(MyPreference.ACCESS_TOKEN_KEY) ?: ""
        val stream = intent.getStringExtra(MyPreference.STREAM_NAME) ?: ""

        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Nats Service")
            .setContentText("Syntropy Data Layer Status:Connected")
            .setSmallIcon(R.drawable.baseline_computer_24)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)

        startNats(natsUrl, accessToken, stream)

        return START_NOT_STICKY
    }

    private fun startNats(url: String, accessToken: String, stream: String) {
        natsProvider = NatsProvider(accessToken, url, stream)
        val connectionMessageHandler = MessageHandler { connectionMsg ->
            val connectionResponse = String(connectionMsg.data, StandardCharsets.UTF_8)
            println("Connection Message: $connectionResponse")
        }

        val fromMessageHandler = MessageHandler { connectionMsg ->
            //  val connectionResponse = String(connectionMsg.data, StandardCharsets.UTF_8)
            dataUpdateListener?.onDataFromDataLayer(connectionMsg.data)
        }

        connectJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                natsProvider?.connect(connectionMessageHandler)
                natsProvider?.subscribe(fromMessageHandler)

                // Get a handler that can be used to post to the main thread
                var mainHandler: Handler = Handler(applicationContext.getMainLooper());

                var myRunnable = Runnable { natsConnectionSuccess() }
                mainHandler.post(myRunnable);


            } catch (e: Exception) {
                dataUpdateListener?.error(e)
                connectJob?.cancel()
                connectJob = null
            }
        }
        connectJob?.start()
    }

    override fun onDestroy() {
        // Here is a good place to stop the LocationEngine.
        unregisterReceiver(serviceBroadcastReceiver)
        connectJob?.cancel()
        connectJob = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder {
        // super.onBind(intent)
        return binder;
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Nats Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }


    fun setDataUpdateListener(listener: DataUpdateListener) {
        this.dataUpdateListener = listener
    }

    private fun sendDataToActivity(data: String) {
        dataUpdateListener?.onDataFromDataLayer(data.toByteArray()) //user data
    }

    private fun connectionMessageHandler(message: Message) {
        dataUpdateListener?.connectionMessageHandler(message)
    }

    private fun natsConnectionSuccess() {

        dataUpdateListener?.natsConnectionSuccess()

    }


}