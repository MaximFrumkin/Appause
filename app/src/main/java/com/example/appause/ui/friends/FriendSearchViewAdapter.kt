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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendSearchViewAdapter() : RecyclerView.Adapter<FriendSearchViewAdapter.FriendSearchViewHolder>() {

     private var friendsSearchResult : List<UserProfile> = emptyList()

    fun updateData(lst : List<UserProfile>) {
        friendsSearchResult = lst
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendSearchViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.friends_search_item, parent, false)
        return FriendSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendSearchViewHolder, i: Int) {
        holder.displayName.text = friendsSearchResult[i].name
        holder.email.text = friendsSearchResult[i].email
    }

    override fun getItemCount(): Int {
        return friendsSearchResult.size
    }

    inner class FriendSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var displayName: TextView
        var email: TextView

        init {
            displayName = itemView.findViewById(R.id.displayName)
            email = itemView.findViewById(R.id.email)

            val button = itemView.findViewById<Button>(R.id.addFriend)
            button.setOnClickListener {view : View ->
                addFriend(view)
            }
        }

        private fun addFriend(view: View) {
            // todo: clean the raw input to avoid injection attack
            val to = email.getText().toString();
            val from = CurrentUser.user.email
            Log.d("TAG", "Sending to $to")

            val db = Firebase.firestore
            val TAG = "MyActivity"

            val collectionRef = db.collection("users")
            collectionRef.whereEqualTo("email", to)
                .get()
                .addOnSuccessListener { result ->
                    // todo: clean it up by just taking first index.
                    for (document in result) {
                        document.reference.update("friendRequests", FieldValue.arrayUnion(from))
                    }
                }
                .addOnFailureListener { error ->
                    Log.e(TAG, "Error adding new value to list: ", error)
                }
        }
    }
}
