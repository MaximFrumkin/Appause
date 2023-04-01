package com.example.appause

import android.content.Context
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.appause.CurrentUser.user

// ADAPTED FROM: https://medium.com/@mendhie/send-device-to-device-push-notifications-without-server-side-code-238611c143

class MileStoneCommunicationManager constructor(context: Context, subscriptionManager: SubscriptionManager) {
    private val ctx: Context
    private val subManager: SubscriptionManager

    init {
        ctx = context
        subManager = subscriptionManager
    }

    fun updateFriendsOnMileStone(streak: Int) {
        val title = "Congratulate " + user.displayName + "!"
        val message = "They hit a $streak-day streak!"
        val notificationManager = AppauseNotificationManager(ctx, subManager.getOwnTopic(), title, message)
        notificationManager.send()
    }

}
