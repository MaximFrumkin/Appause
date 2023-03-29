package com.example.appause.ui.friends

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appause.R
import com.example.appause.UserProfile
import com.example.appause.databinding.FragmentReportsBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendsSearchActivity : AppCompatActivity() {
    private var _binding: FragmentReportsBinding? = null

    private lateinit var searchView: SearchView
    private lateinit var searchResult: List<UserProfile>
    private lateinit var adapter: FriendSearchViewAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_search)
        setContentView(R.layout.friend_search)
//        val view = inflater.inflate(R.layout.friend_search.xml, container, false)

        // Initialize the SearchView
        searchView = findViewById(R.id.friend_search_view)

        // Set a listener to handle search events
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                updateUsersList(query)

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
//        searchResult = listOf(UserProfile("Yousuf", "yafroze@uwaterloo.ca"), UserProfile("Sergiu", "serguipocol@uwaterloo.ca"), UserProfile("maxim", "maximgenius@uwaterloo.ca"))
        searchResult = emptyList()
        adapter.updateData(searchResult)

        val button = findViewById<Button>(R.id.requests)
        button.setOnClickListener {
            val intent = Intent(this, FriendRequestActivity::class.java)
            startActivity(intent)
        }
    }

    // todo: make a better name for the function
    private fun updateUsersList(query: String?) {
        val db = Firebase.firestore
        val TAG = "MyActivity"

        // TODO: Have an OR condition for users to search either using a name or email.
        // todo: Make the query lowercase to make search case insensitive
        // Todo: add the egrep logic for partial recognition
        val usersRef = db.collection("users")
        usersRef
            .whereEqualTo("name", query)
            .get()
            .addOnSuccessListener { documents ->
                // todo: make sure the result doesn't show the current user
                val filteredList = documents.map { data ->
                    UserProfile(data.get("name") as String, data.get("email") as String)
                }
                searchResult = filteredList
                adapter.updateData(searchResult)

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}