package com.example.appause

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.appause.CurrentUser.user
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import org.json.JSONException
import org.json.JSONObject

// ADAPTED FROM: https://medium.com/@mendhie/send-device-to-device-push-notifications-without-server-side-code-238611c143

class SubscriptionManager constructor(context: Context) {
    private val TAG = "NetworkManager"
    private val ctx: Context

    init {
        ctx = context
    }

    fun getOwnTopic(): String {
        val userId = getUserDocId(user)
        return "$userId.milestones"
    }

    private fun getFriendTopic(friendId: String): String {
        return "$friendId.milestones"
    }

    private fun getFriendIds(user: FirebaseUser): List<String> {
        val db = Firebase.firestore
        val TAG = "MainActivity"
        val usersRef = db.collection("users")
        var friends = mutableListOf<String>()
        usersRef.whereEqualTo("email", user.email)
            .get()
            .addOnSuccessListener { documents ->
                assert(documents.size() == 1)

                for (doc in documents) {
                    val friendsList = doc.data["friends"] as List<String>
                    for (friend in friendsList) {
                        friends.add(friend)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        return friends
    }

    fun ensureSubscribedToFriends() {
        val friendIds = getFriendIds(user)
        val friendMilestoneTopics = friendIds.map { id -> getFriendTopic(id) }
        for (friendTopic in friendMilestoneTopics) {
            Firebase.messaging.subscribeToTopic(friendTopic)
                .addOnCompleteListener { task ->
                    var msg = "Subscribed"
                    if (!task.isSuccessful) {
                        msg = "Subscribe failed"
                    }
                    Log.d("SUBSCRIBING", msg)
                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
                }
        }
    }
}

