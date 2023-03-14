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

class FriendRequestActivity : AppCompatActivity() {
    private var _binding: FragmentReportsBinding? = null

    private lateinit var requests: List<UserProfile>
    private lateinit var adapter: FriendSearchViewAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_search)
        setContentView(R.layout.activity_friend_request)
//        val view = inflater.inflate(R.layout.friend_search.xml, container, false)


        // Initialize the RecyclerView and adapter
        recyclerView = findViewById(R.id.recycler_view)
        adapter = FriendSearchViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


//         Initialize the item list and submit it to the adapter
//        searchResult = listOf(UserProfile("Yousuf", "yafroze@uwaterloo.ca"), UserProfile("Sergui", "serguipocol@uwaterloo.ca"), UserProfile("maxim", "maximgenius@uwaterloo.ca"))
        requests = emptyList()
        adapter.updateData(requests)
    }
}