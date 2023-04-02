package com.example.appause.ui.friends

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appause.CurrentUser
import com.example.appause.R
import com.example.appause.UserProfile
import com.example.appause.databinding.FragmentReportsBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class FriendsSearchActivity : AppCompatActivity() {
    private var _binding: FragmentReportsBinding? = null

    private lateinit var searchView: SearchView
    private lateinit var searchResult: List<UserProfile>
    private lateinit var adapter: FriendSearchViewAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friend_search)

        // Initialize the SearchView
        searchView = findViewById(R.id.friend_search_view)

        // Set a listener to handle search events
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    updateUsersList(query.lowercase(Locale.getDefault()))
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Initialize the RecyclerView and adapter
        recyclerView = findViewById(R.id.recycler_view)
        adapter = FriendSearchViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


//         Initialize the item list and submit it to the adapter
        searchResult = emptyList()
        adapter.updateData(searchResult)

        val button = findViewById<Button>(R.id.requests)
        button.setOnClickListener {
            val intent = Intent(this, FriendRequestActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateUsersList(query: String?) {
        val db = Firebase.firestore
        val TAG = "Firebase"

        // Queries are case insensitive, handles partial query and matches against all possible
        // results. However, currently, only search can be done against user's name
        var usersRef = db.collection("users")

        if (query != null) {
            usersRef
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .get()
                .addOnSuccessListener { documents ->
                    // We do not show the user itself asthe query result
                    val filteredList = documents.map { data ->
                        UserProfile(data.get("name") as String, data.get("email") as String)
                    }.filter { profile -> profile.email != CurrentUser.user.email}
                    searchResult = filteredList

                    adapter.updateData(searchResult)
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }
}