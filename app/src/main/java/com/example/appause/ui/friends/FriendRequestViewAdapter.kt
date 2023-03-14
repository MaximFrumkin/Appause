package com.example.appause.ui.friends

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appause.CurrentUser
import com.example.appause.MainActivity
import com.example.appause.R
import com.example.appause.UserProfile
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendRequestViewAdapter() : RecyclerView.Adapter<FriendRequestViewAdapter.FriendRequestViewHolder>() {

    private var friendRequestsList : List<UserProfile> = emptyList()

    fun updateData(lst : List<UserProfile>) {
        friendRequestsList = lst
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendRequestViewHolder {
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
//            // todo: clean the raw input to avoid injection attack
//            val to = email.getText().toString();
//            // TODO: Uncomment the line below. Comment out because, I need to sign in to get user.email
//            val from = CurrentUser.user.email
//            Log.d("TAG", "Sending to $to")
//
//            val db = Firebase.firestore
//            val TAG = "MyActivity"
//
//            val collectionRef = db.collection("users")
//            collectionRef.whereEqualTo("email", to)
//                .get()
//                .addOnSuccessListener { result ->
//                    // todo: clean it up by just taking first index.
//                    for (document in result) {
//                        // todo: change the double quotation
//                        // todo: check for current request already existing
//                        document.reference.update("friendRequests", listOf(from))
//                    }
//                }
//                .addOnFailureListener { error ->
//                    Log.e(TAG, "Error adding new value to list: ", error)
//                }
        }

        private fun declineRequest(view : View) {

        }
    }
}
