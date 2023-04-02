package com.example.appause

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class AppauseFirebaseInstanceIDService : FirebaseMessagingService() {
    private val TAG = "AppauseMessaging"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "msg received: ${remoteMessage.from}")
        if (remoteMessage.notification != null) {
            if (remoteMessage.from?.endsWith(".milestones") == true) {
                requestCongratulations(
                    remoteMessage,
                    remoteMessage.notification?.title,
                    remoteMessage.notification?.body
                )
            }
        }
    }

    @SuppressLint("LongLogTag")
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestCongratulations(
        remoteMessage: RemoteMessage,
        title: String?,
        body: String?
    ) {
        Log.d(TAG, "SUCCESS NOTIFICATION RECEIVED!!!")
        super.onMessageReceived(remoteMessage)
        val channelId = "Appause"
        val channel = NotificationChannel(
            channelId,
            "Appause",
            NotificationManager.IMPORTANCE_HIGH
        )

        val resultIntent = Intent(this, MainActivity::class.java)
        resultIntent.putExtra("milestone", remoteMessage.data.get("milestone"))
        resultIntent.putExtra("friendid", remoteMessage.data.get("friendid"))
        resultIntent.putExtra("friendname", remoteMessage.data.get("friendname"))
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notification = NotificationCompat.Builder(this)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setSmallIcon(R.mipmap.ic_launcher).setChannelId(channelId)
            .setContentIntent(resultPendingIntent)
            .build()
        val manager = NotificationManagerCompat.from(applicationContext)
        manager.createNotificationChannel(channel)
        manager.notify(12345, notification)
    }
}
