package com.example.appause.ui.friends

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appause.R
import com.example.appause.UserProfile
import com.example.appause.databinding.FragmentReportsBinding
import com.example.appause.ui.reports.FriendSearchViewAdapter
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

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

//        val view = inflater.inflate(R.layout.fragment_friend_search, container, false)
//
//        val recyclerView: RecyclerView = view!!.findViewById(R.id.friend_search_view)
//        recyclerView.layoutManager = LinearLayoutManager(activity)
//        val adapter : FriendSearchViewAdapter = FriendSearchViewAdapter()
//        adapter.updateData(listOf(UserProfile("Yousuf", "yafroze@uwaterloo.ca"), UserProfile("Sergui", "serguipocol@uwaterloo.ca"), UserProfile("maxim", "maximgenius@uwaterloo.ca")))
//        recyclerView.adapter = adapter
//
//
//        return view











        val view = inflater.inflate(R.layout.fragment_friend_search, container, false)

        // Initialize the SearchView
        searchView = view.findViewById(R.id.friend_search_view)

        // Set a listener to handle search events
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission
//                updateUsersList(query)
//
//                Log.d("TAG", "After submissiont the list has the following items")
//                for (document in searchResult) {
//                    Log.d("TAG", "${document.name}")
//                }

                    // Perform network call on a background thread

                    updateUsersList(query)

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                // Filter the item list based on the search query
//                val filteredList = searchResult.filter { item ->
//                    item.name.contains(newText.orEmpty(), ignoreCase = true)
//                }
////
////                // Update the adapter with the filtered list
//                adapter.updateData(filteredList)
//                Log.d("TAG", "TEXT CHANGED")

                return false
            }
        })

        // Initialize the RecyclerView and adapter
        recyclerView = view.findViewById(R.id.recycler_view)
        adapter = FriendSearchViewAdapter()
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

        // TODO: Have an OR condition for users to search either using a name or email
        val usersRef = db.collection("users")
        usersRef
            .whereEqualTo("name", query)
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "Got somethings. Adding right now")

                val filteredList = documents.map { data ->
                    Log.d(TAG, "email - ${data.get("email")}")
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