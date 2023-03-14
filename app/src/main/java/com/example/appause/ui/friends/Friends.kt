package com.example.appause.ui.friends

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.appause.R


class Friends : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.friends, container, false)
        val button = view.findViewById<Button>(R.id.addFriend)
        button.setOnClickListener {
            val intent = Intent(context, FriendsSearchActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}