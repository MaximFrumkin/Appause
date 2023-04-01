package com.example.appause

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.appause.CurrentUser.user
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AppauseFirebaseInstanceIDService : FirebaseMessagingService() {
    private val TAG = "AppauseMessaging"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "msg received: ${remoteMessage.from}")
        if (remoteMessage.notification != null) {
            showNotification(remoteMessage.notification?.title, remoteMessage.notification?.body)
        }
    }

    @SuppressLint("LongLogTag")
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
            val db = Firebase.firestore
            val TAG = "AppauseFirebaseInstanceIDService"
            val userDocId = getUserDocId(user)
            val userRef = db.collection("FCMTokens").document("$userDocId")
            userRef.update("value", token)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }


    }

    private fun showNotification(title: String?, body: String?) {
        Log.d(TAG, "SUCCESS NOTIFICATION RECEIVED!!!")
    }

    companion object {
        fun getTokenFromService(context: Context): String? {
            return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty")
        }
    }
}
