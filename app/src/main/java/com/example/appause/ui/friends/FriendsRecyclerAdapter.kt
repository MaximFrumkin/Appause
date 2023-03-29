package com.example.appause.ui.friends

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.appause.R

class FriendsRecyclerAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<FriendsRecyclerAdapter.ViewHolder>() {
    private val friendName = arrayOf("Alexander", "Emma", "Nate L.", "You", "Jordan")

    private val friendGoalRatioAchievedText = arrayOf("Achieved 5/5 of his goals!", "Achieved 6/6 of her goals!", "Achieved 2/2 of his goals!", "Achieved 1/4 of your goals!", "Achieved 3/3 of his goals!")

    private val goalsAchieved = arrayOf(5, 6, 2, 1, 3)
    private val totalGoals = arrayOf(5, 6, 2, 4, 3)

    interface OnItemClickListener {
        fun onItemClick(imageNo: Int, name: String, goalRatioAchieved: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.friends_list_item, parent, false)
            return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: FriendsRecyclerAdapter.ViewHolder, i: Int) {
        val image : Int = if (goalsAchieved[i] / totalGoals[i] == 1) {
            R.drawable.gold_trophy
        } else {
            R.drawable.silver_trophy

        }

        holder.trophy.setImageResource(image)
        holder.friendName.text = friendName[i]
        holder.friendGoalRatioAchievedText.text = friendGoalRatioAchievedText[i]
        holder.bind(image, friendName[i], friendGoalRatioAchievedText[i], listener)
    }

    override fun getItemCount(): Int {
        return friendName.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var trophy: ImageView
        var friendName: TextView
        var friendGoalRatioAchievedText: TextView

        init {
            trophy = itemView.findViewById(R.id.trophy)
            friendName = itemView.findViewById(R.id.friendName)
            friendGoalRatioAchievedText = itemView.findViewById(R.id.friendGoalRatioAchievedText)
        }


        fun bind(imageNo: Int, name: String, goalRatioAchieved: String, listener: OnItemClickListener) {
            Log.d("Friends", "bind: listener is set: name")
//            itemView.rootView
            val view = itemView.findViewById<CardView>(R.id.friend_card)
            view.setOnClickListener {
                Log.d("Friends", "listener is set: $name")
                listener.onItemClick(imageNo, name, goalRatioAchieved)
            }
        }
    }
}