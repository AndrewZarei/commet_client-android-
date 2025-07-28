package com.blockchain.commet.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.blockchain.commet.R
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.service.MessageCheckService
import com.blockchain.commet.worker.ServiceCheckerWorker
import com.solana.Config
import java.util.concurrent.TimeUnit

class Splashscreen : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Config.network = SharedPrefsHelper.getSharedPrefsHelper().get("network","Dev")
        findViewById<ImageView>(R.id.image).startAnimation(AnimationUtils.loadAnimation(this,
            R.anim.splash_fade_in
        ))
        findViewById<TextView>(R.id.textView).startAnimation(AnimationUtils.loadAnimation(this,
            R.anim.splash_fade_in
        ))
        Handler().postDelayed({
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }, 1200)
    }

}