package com.example.appause.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appause.R
import com.example.appause.databinding.FragmentReportsBinding
import com.example.appause.ui.reports.RecyclerAdapter

class FriendsFragment : Fragment() {
    private var _binding: FragmentReportsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.friends, container, false)

//        val recyclerView: RecyclerView = view.findViewById(R.id.friends_recycler_view)
//        recyclerView.layoutManager = LinearLayoutManager(activity)
//        recyclerView.adapter = FriendsRecyclerAdapter(listener)
        return view

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}