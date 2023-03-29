package com.example.appause.ui.friends

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appause.R

class FriendReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friends_report_item) // set the layout for the activity

        // pass data to a detailed report of a friend on Friends page
        findViewById<ImageView>(R.id.trophy).setImageResource(intent.getIntExtra("IMAGE_NO", R.drawable.silver_trophy))
        findViewById<TextView>(R.id.friendName).text = intent.getStringExtra("NAME")
        findViewById<TextView>(R.id.friendGoalRatioAchievedText).text = intent.getStringExtra("GAOL_RATIO_ARCHIVED")

    }
}