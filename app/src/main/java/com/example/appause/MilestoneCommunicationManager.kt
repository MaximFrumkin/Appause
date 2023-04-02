package com.example.appause

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.appause.CurrentUser.user
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking

class MileStoneCommunicationManager constructor(context: Context) {
    private val ctx: Context

    init {
        ctx = context
    }

    fun updateFriendsOnMileStone(streak: Int) {
        // First, clear the congratulator list!
        val userId = getUserDocId(user)
        Firebase.firestore.collection("users/$userId").document().update(
            mutableMapOf(
                "congratulators" to emptyArray<String>()
            ) as Map<String, Any>
        )
        val title = "Congratulate " + user.displayName + "!"
        val message = "They hit a $streak-day streak!"
        val notificationManager = AppauseNotificationManager(
            ctx, SubscriptionManager.getOwnTopic(),
            title, message
        )
        notificationManager.send(streak, userId, user.displayName)
    }

}
