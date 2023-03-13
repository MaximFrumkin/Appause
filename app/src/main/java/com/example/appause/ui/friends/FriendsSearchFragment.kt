package com.example.appause.ui.friends

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appause.MainActivity
import com.example.appause.R
import com.example.appause.UserProfile
import com.example.appause.databinding.FragmentReportsBinding
import com.example.appause.ui.reports.FriendSearchViewAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FriendSearchFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null

    private lateinit var searchView: SearchView
    private lateinit var searchResult: List<UserProfile>
    private lateinit var adapter: FriendSearchViewAdapter
    private lateinit var recyclerView: RecyclerView





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_friend_search, container, false)

        // Initialize the SearchView
        searchView = view.findViewById(R.id.friend_search_view)

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
        recyclerView = view.findViewById(R.id.recycler_view)
        adapter = FriendSearchViewAdapter(activity as MainActivity)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter


//         Initialize the item list and submit it to the adapter
//        searchResult = listOf(UserProfile("Yousuf", "yafroze@uwaterloo.ca"), UserProfile("Sergui", "serguipocol@uwaterloo.ca"), UserProfile("maxim", "maximgenius@uwaterloo.ca"))
        searchResult = emptyList()
        adapter.updateData(searchResult)

        return view

    }

    private fun updateUsersList(query: String?) {
        val db = Firebase.firestore
        val TAG = "MyActivity"

        // TODO: Have an OR condition for users to search either using a name or email.
        // todo: Make the query lowercase to make search case insensitive
        val usersRef = db.collection("users")
        usersRef
            .whereEqualTo("name", query)
            .get()
            .addOnSuccessListener { documents ->

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}