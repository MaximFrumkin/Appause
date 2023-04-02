package com.example.appause.ui.friends

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.appause.CurrentUser
import com.example.appause.R
import com.example.appause.SubscriptionManager
import com.example.appause.UserProfile
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendRequestViewAdapter() : RecyclerView.Adapter<FriendRequestViewAdapter.FriendRequestViewHolder>() {

    private var friendRequestsList : MutableList<UserProfile> = mutableListOf()
    private lateinit var mContext: Context

    fun addUserProfile(user : UserProfile) {
        friendRequestsList.add(user)
        notifyDataSetChanged()
    }

    fun removeUserProfile(user : UserProfile) {
        friendRequestsList.remove(user)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendRequestViewHolder {
        mContext = parent.context
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.friend_request_item, parent, false)
        return FriendRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendRequestViewHolder, i: Int) {
        holder.displayName.text = friendRequestsList[i].name
        holder.email.text = friendRequestsList[i].email
    }

    override fun getItemCount(): Int {
        return friendRequestsList.size
    }

    inner class FriendRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var displayName: TextView
        var email: TextView

        init {
            displayName = itemView.findViewById(R.id.displayName)
            email = itemView.findViewById(R.id.email)

            val acceptButton = itemView.findViewById<Button>(R.id.accept)
            acceptButton.setOnClickListener {view : View ->
                acceptRequest(view)
            }

            val declineButton = itemView.findViewById<Button>(R.id.decline)
            declineButton.setOnClickListener {view : View ->
                declineRequest(view)
            }
        }

        private fun acceptRequest(view: View) {
            val name : String = itemView.findViewById<TextView>(R.id.displayName).text.toString()
            val email : String = itemView.findViewById<TextView>(R.id.email).text.toString()

            removeUserProfile(UserProfile(name, email))
            var getIdTask = CurrentUser.getUsersId(email)

            getIdTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val senderId = task.result.toString()
                    removeRequest(senderId, CurrentUser.user.email.toString())
                } else {
                    Log.e("Firebase", "Error removing request", task.exception)
                }
            }

            getIdTask = CurrentUser.getUsersId(email)
            getIdTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val senderId = task.result.toString()
                    addFriendInDB(senderId, CurrentUser.user.email.toString())
                } else {
                    Log.e("Firebase", "Error removing request", task.exception)
                }
            }

            SubscriptionManager.ensureSubscribedToFriends(mContext)
        }

        private fun declineRequest(view : View) {
            val name : String = itemView.findViewById<TextView>(R.id.displayName).text.toString()
            val email : String = itemView.findViewById<TextView>(R.id.email).text.toString()

            removeUserProfile(UserProfile(name, email))
            val getIdTask = CurrentUser.getUsersId(email)

            getIdTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val senderId = task.result.toString()
                    removeRequest(senderId, CurrentUser.user.email.toString())
                } else {
                    Log.e("Firebase", "Error removing request", task.exception)
                }
            }
        }

        private fun removeRequest(requesterId: String, requestedUserEmail: String) {
            val db = Firebase.firestore
            val collectionRef = db.collection("users")
            collectionRef.whereEqualTo("email", requestedUserEmail)
                .limit(1)
                .get()
                .addOnSuccessListener { result ->
                    // FieldValue.arrayUnion makes a union of distinct elements. Hence a user can't
                    // send multiple requests to the user before being declined.
                    if (!result.isEmpty) {
                        val document = result.first()
                        document.reference.update(
                            "friendRequests",
                            FieldValue.arrayRemove(requesterId)
                        )
                    }
                }
                .addOnFailureListener { error ->
                    Log.e("Firebase", "Error adding new value to list: ", error)
                }
        }


        private fun addFriendInDB(requesterId: String, requestedUserEmail: String) {
            val db = Firebase.firestore
            val collectionRef = db.collection("users")
            collectionRef.whereEqualTo("email", requestedUserEmail)
                .limit(1)
                .get()
                .addOnSuccessListener { result ->
                    if (!result.isEmpty) {
                        val document = result.first()
                        document.reference.update(
                            "friends",
                            FieldValue.arrayUnion(requesterId)
                        )
                    }
                }
                .addOnFailureListener { error ->
                    Log.e("Firebase", "Error adding new value to list: ", error)
                }
        }
    }
}
