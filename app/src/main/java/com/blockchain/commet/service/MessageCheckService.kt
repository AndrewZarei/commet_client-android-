package com.blockchain.commet.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import com.blockchain.commet.MyApplication.Companion.appContext
import com.blockchain.commet.R
import com.blockchain.commet.data.database.DBHelper
import com.blockchain.commet.data.database.Logs
import com.blockchain.commet.data.sharepref.SharedPrefsHelper
import com.blockchain.commet.util.getLargeNotification
import com.blockchain.commet.util.getSimpleNotification
import com.blockchain.commet.util.showSimpleNotification
import com.blockchain.commet.view.fragment.conversations.ConversationsFragment
import com.example.mysolana.conversations.ConversationsComponent
import com.example.mysolana.conversations.ConversationsInterface
import com.example.mysolana.conversations.StateConversations
import com.solana.models.buffer.ConversationItemModel
import com.solana.models.buffer.ProfileModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MessageCheckService : Service(), ConversationsInterface {

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var component: ConversationsComponent

    private fun checkForNewMessages() {
        component = ConversationsComponent(this@MessageCheckService)
        component.getConversations(SharedPrefsHelper.getSharedPrefsHelper().get("id"))
    }

    override fun getConversations(
        error: String?,
        profileModel: ProfileModel?,
        stateConversations: StateConversations?
    ) {
        if (error.equals("SUCCESS")) {
            var conversationItemModel = DBHelper(this).GetConversations()
            profileModel?.conversation_list?.size?.let {
                if (it > conversationItemModel.size) {
                    applicationContext.getSimpleNotification("New Message","")
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1002,
                applicationContext.getSimpleNotification("Service",""),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(1002, applicationContext.getSimpleNotification("Service",""))
        }

        handler = Handler(Looper.getMainLooper())
        object : Runnable {
            override fun run() {
                checkForNewMessages()
                handler.postDelayed(this, 10000)
            }
        }.also { runnable = it }
        handler.post(runnable)

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }

}
