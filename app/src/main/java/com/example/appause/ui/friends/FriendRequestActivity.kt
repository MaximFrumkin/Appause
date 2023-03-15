package com.example.appause.ui.friends

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appause.CurrentUser
import com.example.appause.R
import com.example.appause.UserProfile
import com.example.appause.databinding.FragmentReportsBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendRequestActivity : AppCompatActivity() {

    private lateinit var adapter: FriendRequestViewAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_request)


        // Initialize the RecyclerView and adapter
        recyclerView = findViewById(R.id.recycler_view)
        adapter = FriendRequestViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


//         Initialize the item list and submit it to the adapter
        populateFriendRequestList()
//        adapter.addUserProfile(requests)
    }

    private fun populateFriendRequestList() {

        val db = Firebase.firestore
        val from = CurrentUser.user.email.toString()

        val collectionRef = db.collection("users")
        collectionRef.whereEqualTo("email", from).limit(1)
            .get()
            .addOnSuccessListener { result ->
                val lst = result.documents[0].get("friendRequests") as List<String>
                createUsersUsingId(lst)

            }
            .addOnFailureListener { error ->
                Log.e("Firebase", "Error fetching requests ", error)
            }
    }

    private fun createUsersUsingId(lst : List<String>) {
        val collection = Firebase.firestore.collection("users")
        for (id in lst) {
            collection
                .document(id)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.get("name")
                        val email = document.get("email")

                        if (name!= null && email != null) {
                            adapter.addUserProfile(UserProfile(name.toString(), email.toString()))
                        }

                    } else {
                        // Document does not exist
                        Log.e("Firebase", "Didnt find the user with the given id")
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors that occur while trying to retrieve the document
                }


        }
    }

}