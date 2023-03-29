package com.example.appause.ui.friends

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appause.R


class Friends : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("Friends.onCreateView (PR)")
        Log.d("Friends", "onCreateView")
        val view = inflater.inflate(R.layout.friends, container, false)
        val button = view.findViewById<Button>(R.id.addFriend)
        button.setOnClickListener {
            val intent = Intent(context, FriendsSearchActivity::class.java)
            startActivity(intent)
        }


        val listener = object : FriendsRecyclerAdapter.OnItemClickListener {

            override fun onItemClick(imageNo: Int, name: String, goalRatioAchieved: String) {
                println("Item clicked: $name")
            val intent = Intent(context, FriendReportActivity::class.java).apply {
                putExtra("IMAGE_NO", imageNo)
                putExtra("NAME", name)
                putExtra("GAOL_RATIO_ARCHIVED", goalRatioAchieved)
            }
            startActivity(intent)
            }
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.friends_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = FriendsRecyclerAdapter(listener)

        return view
    }
}