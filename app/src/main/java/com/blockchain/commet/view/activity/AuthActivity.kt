package com.blockchain.commet.view.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.blockchain.commet.R
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.databinding.ActivityAuthBinding
import com.blockchain.commet.service.MessageCheckService
import com.blockchain.commet.worker.ServiceCheckerWorker
import java.util.concurrent.TimeUnit

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private var TimeBackPressed: Long = 0
    private val TIME_BETWEEN_TWO_BACK = 2000

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                startMyService()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        handleIncomingLoginIntent(intent)
        onBackClick()
        setConfig()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingLoginIntent(intent)
    }

    private fun handleIncomingLoginIntent(intent: Intent) {
        if (intent.action == "com.blockchain.commet.LOGIN") {
            val token = intent.getStringExtra("token")
            if (token.isNullOrEmpty() || token != "YXP6b8dtQU5T0fShMOdB") {
                setResult(RESULT_CANCELED)
            } else {
                val resultIntent = Intent()
                resultIntent.putExtra("userName", SharedPrefsHelper.getSharedPrefsHelper()["username"])
                resultIntent.putExtra("publicKey", SharedPrefsHelper.getSharedPrefsHelper()["base_pubkey"])
                setResult(RESULT_OK, resultIntent)
            }
            finish()
        }
    }

    private fun onBackClick() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (TimeBackPressed + TIME_BETWEEN_TWO_BACK > System.currentTimeMillis() || supportFragmentManager.fragments.size > 1) {
                    finish()
                    return
                } else {
                    Toast.makeText(applicationContext, "Double Touch To Exit..", Toast.LENGTH_SHORT).show()
                }
                TimeBackPressed = System.currentTimeMillis()
            }
        })
    }

    private fun setConfig(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissions = mutableListOf<String>()

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
            }

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC)
                != PackageManager.PERMISSION_GRANTED) {
                permissions.add(android.Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC)
            }

            if (permissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 1001)
            } else {
                startMyService()
            }
        } else {
            startMyService()
        }

        val request = PeriodicWorkRequestBuilder<ServiceCheckerWorker>(15, TimeUnit.MINUTES)
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "service_checker", ExistingPeriodicWorkPolicy.KEEP, request
        )
    }

    private fun startMyService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(this, MessageCheckService::class.java)
            startForegroundService(intent)
        } else {
            val intent = Intent(this, MessageCheckService::class.java)
            startService(intent)
        }
    }
}