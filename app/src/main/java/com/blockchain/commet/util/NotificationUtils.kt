package com.blockchain.commet.util

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.blockchain.commet.R

fun Context.showSimpleNotification(
    notificationId: Int,
    textTitle: String = "",
    textContent: String = ""
) {
    with(NotificationManagerCompat.from(this)) {
        // notificationId is a unique int for each notification that you must define
        if (ActivityCompat.checkSelfPermission(
                this@showSimpleNotification,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notify(notificationId, getSimpleNotification(textTitle, textContent))
    }
}

fun Context.getSimpleNotification(
    textTitle: String,
    textContent: String
): Notification {
    createNotificationChannel()
    val channelId = "programChannel"
    return NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.communication)
        .setContentTitle(textTitle)
        .setContentText(textContent)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
}

fun Context.getLargeNotification(
    textTitle: String
): Notification {
    createNotificationChannel()
    val channelId = "programChannel"
    return NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.communication)
        .setLargeIcon(
            BitmapFactory.decodeResource(
                resources,
                R.drawable.communication
            )
        )
        .setVibrate(longArrayOf(0L))
        .setContentTitle(textTitle)
        .setAutoCancel(true)
        .build()
}

fun Context.createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "programChannel"
        val name = "program_channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = "channel_description"
        }
        // Register the channel with the system
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}