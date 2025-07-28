package com.blockchain.commet.view.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blockchain.commet.databinding.ActivityFullscreenBinding

class FullscreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullscreenBinding
    private var mContentView: View? = null
    private val mHideHandler = Handler(Looper.myLooper()!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())

        mContentView = binding.fullscreenContent
        val intent = getIntent()
        val img = intent.getStringExtra("image")

        val decodedString = Base64.decode(img, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        binding.fullscreenContent.setImageBitmap(decodedByte)
    }
}