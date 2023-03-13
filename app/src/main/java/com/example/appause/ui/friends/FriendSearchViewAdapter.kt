package com.example.appause.ui.reports

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appause.MainActivity
import com.example.appause.R
import com.example.appause.UserProfile
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendSearchViewAdapter(mainActivity: MainActivity) : RecyclerView.Adapter<FriendSearchViewAdapter.ViewHolder>() {

     private var friendsSearchResult : List<UserProfile> = emptyList()
     private val mainActivity: MainActivity = mainActivity

    fun updateData(lst : List<UserProfile>) {
        friendsSearchResult = lst
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.friends_search_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {
        holder.displayName.text = friendsSearchResult[i].name
        holder.email.text = friendsSearchResult[i].email
    }

    override fun getItemCount(): Int {
        return friendsSearchResult.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
            val to = email.getText().toString();
            // TODO: Uncomment the line below. Comment out because, I need to sign in to get user.email
//            val from = mainActivity.user.email
            Log.d("TAG", "Sending to $to")

            val db = Firebase.firestore
            val TAG = "MyActivity"

            val collectionRef = db.collection("users")
            collectionRef.whereEqualTo("email", to)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        // todo: change the double quotation
                        document.reference.update("friendRequests", listOf("from"))
                    }
                }
                .addOnFailureListener { error ->
                    Log.e(TAG, "Error adding new value to list: ", error)
                }
        }
    }
}
