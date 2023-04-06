package com.example.appause

import android.content.Context
import com.example.appause.CurrentUser.user
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MileStoneCommunicationManager constructor(context: Context) {
    private val ctx: Context

    init {
        ctx = context
    }

    fun updateFriendsOnMileStone(streak: Int) {
        // First, clear the congratulator list!
        val userId = getUserDocIdBlocking(user)
        Firebase.firestore.collection("users").document("$userId").update(
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
