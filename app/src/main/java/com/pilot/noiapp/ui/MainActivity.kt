package com.pilot.noiapp.ui

import android.Manifest
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings.Secure
import android.view.View
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.pilot.noiapp.R
import com.pilot.noiapp.databinding.ActivityMainBinding
import com.pilot.noiapp.manager.MyPreference
import com.pilot.noiapp.model.DataLayerMessage
import com.pilot.noiapp.service.DataUpdateListener
import com.pilot.noiapp.service.NatsService
import com.pilot.noiapp.utils.toDataLayerMessage
import com.pilot.noiapp.utils.toJson
import com.pilot.noiapp.viewmodels.ConfigurationViewModel
import com.pilot.noiapp.viewmodels.InitializeFirebaseViewModel
import com.pilot.noiapp.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.nats.client.Message
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DataUpdateListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var foregroundService: NatsService? = null
    private var isServiceBound = false
    private val viewModel by viewModels<ConfigurationViewModel>()
    private val firebaseViewModel by viewModels<InitializeFirebaseViewModel>()

    private val sharedViewModel by viewModels<SharedViewModel>()
    private lateinit var androidUID:String

    var rpl: ActivityResultLauncher<Array<String>>? =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(),
            object : ActivityResultCallback<Map<String, Boolean>> {

                override fun onActivityResult(isGranted: Map<String, Boolean>) {
                    var granted = true
                    for (data in isGranted.entries) {
                        if (!data.value) granted = false
                    }
                    if (granted) {
                        println("Permissions granted for api 33+")
                        attachObserver()
                    }
                }
            }
        )
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.POST_NOTIFICATIONS)

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    @Inject
    lateinit var myPreference: MyPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        androidUID = Secure.getString(applicationContext.contentResolver, Secure.ANDROID_ID)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loading.loadingItem.visibility = View.VISIBLE

        // sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!allPermissionsGranted()) {
                stopService()
                rpl!!.launch(REQUIRED_PERMISSIONS)
            } else
                attachObserver()
        } else
            attachObserver()

    }


    private fun attachObserver() {

        firebaseViewModel.cVersionError.observe(this){updateApp->
            if(updateApp){
                updateApp()
            }

        }
        firebaseViewModel.completeEvent.observe(this) { succeeded ->
            if (succeeded) {
                //permessi ok
                //nats configuration retrieved
                startService()


            } else {
                //error
                error(Exception())
                stopService()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun connectionMessageHandler(status: Message) {
        val foo = 0
    }

    override fun natsConnectionSuccess() {
        binding.loading.loadingItem.visibility = View.GONE
        binding.navContainer.findNavController().setGraph(R.navigation.nav_graph_chat)
    }

    private fun updateApp(){
        runOnUiThread(Runnable() {
            val dialog = AlertDialog.Builder(this).create()
            dialog.setMessage(getString(R.string.update_app_msg))
            dialog.setButton(
                DialogInterface.BUTTON_POSITIVE,
                getString(R.string.btn_ok),
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.dismiss()
                        stopService()
                        finish()

                    }

                })

            dialog.show()
        })
    }

    override fun error(e: Exception) {
        binding.loading.loadingItem.visibility = View.GONE
        runOnUiThread(Runnable() {
            val dialog = AlertDialog.Builder(this).create()
            dialog.setMessage(getString(R.string.nats_error_msg))
            dialog.setButton(
                DialogInterface.BUTTON_POSITIVE,
                getString(R.string.btn_ok),
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog?.dismiss()
                        stopService()
                        finish()

                    }

                })

            dialog.show()
        })
    }


    override fun onDestroy() {
     //   stopService()
        super.onDestroy()
    }


    fun startService() {
        val serviceIntent: Intent = Intent(this, NatsService::class.java)
        myPreference.getNatsConfiguration().apply {
            serviceIntent.putExtra(MyPreference.URL, url)
            serviceIntent.putExtra(MyPreference.ACCESS_TOKEN_KEY, accessToken)
            serviceIntent.putExtra(MyPreference.STREAM_NAME, stream)
        }

        ContextCompat.startForegroundService(this@MainActivity, serviceIntent)

        bindService(
            Intent(this, NatsService::class.java), serviceConnection, BIND_AUTO_CREATE
        )

    }

    private fun stopService() {
        val serviceIntent: Intent = Intent(this@MainActivity, NatsService::class.java)
        this.stopService(serviceIntent)
    }


    override fun onStart() {
        super.onStart()
    }


    override fun onStop() {
        super.onStop()
       // unbindService(serviceConnection)
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            val binder: NatsService.LocalBinder = iBinder as NatsService.LocalBinder
            foregroundService = binder.service
            foregroundService?.setDataUpdateListener(this@MainActivity)
            isServiceBound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            foregroundService = null
            isServiceBound = false
        }
    }


    override fun onDataFromDataLayer(data: ByteArray) {
        sharedViewModel.sendMessage(String(data, Charsets.UTF_8).toDataLayerMessage())
    }


    fun sendMessageToDataLayer(msg: String) {
        val jsonStr = DataLayerMessage(androidUID, msg).toJson()
        val broadcast = Intent(NatsService.SEND_TO_DATA_LAYER)
        broadcast.putExtra(NatsService.CHAT_MESSAGE, jsonStr)
        sendBroadcast(broadcast)
    }
}