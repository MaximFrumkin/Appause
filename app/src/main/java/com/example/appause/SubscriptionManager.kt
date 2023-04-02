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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.json.JSONException
import org.json.JSONObject

// ADAPTED FROM: https://medium.com/@mendhie/send-device-to-device-push-notifications-without-server-side-code-238611c143

class SubscriptionManager {
    companion object {
        private val SM_TAG = "SubscriptionManager"

        fun getOwnTopic(): String {
            var userId = getUserDocId(FirebaseAuth.getInstance().currentUser!!)
            Log.d(SM_TAG, "USER_ID EXTRACTED IS ->$userId")
            return "$userId.milestones"
        }

        private fun getFriendTopic(friendId: String): String {
            return "$friendId.milestones"
        }

        private fun getFriendIds(user: FirebaseUser): List<String> {
            val db = Firebase.firestore
            val usersRef = db.collection("users")
            var friends = mutableListOf<String>()
            var friendsTask: QuerySnapshot? = null
            runBlocking {
                friendsTask = usersRef.whereEqualTo("email", user.email)
                    .get().await()
            }

            if (friendsTask != null) {
                val documents = friendsTask!!.documents
                assert(documents.size == 1)

                for (doc in documents) {
                    val friendsList = doc.data?.get("friends") as List<String>
                    for (friend in friendsList) {
                        friends.add(friend)
                    }
                }
            }

            return friends
        }

        fun ensureSubscribedToFriends(ctx : Context) {
            val friendIds = getFriendIds(FirebaseAuth.getInstance().currentUser!!)
            val friendMilestoneTopics = friendIds.map { id -> getFriendTopic(id) }
            for (friendTopic in friendMilestoneTopics) {
                Firebase.messaging.subscribeToTopic(friendTopic)
                    .addOnCompleteListener { task ->
                        var msg = "Subscribed"
                        if (!task.isSuccessful) {
                            msg = "Subscribe failed"
                        }
                        Log.d("SUBSCRIBING", msg)
                    }
            }
        }
    }
}

