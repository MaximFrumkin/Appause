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
import com.example.appause.CurrentUser
import com.example.appause.R
import com.example.appause.SubscriptionManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.concurrent.Flow.Subscription

class FriendsRecyclerAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<FriendsRecyclerAdapter.ViewHolder>() {

    class FriendGoalStatus(val friendName: String, val goalsAchieved: Long, val totalGoals: Long) {

    }

    private val goalStatuses = mutableListOf<FriendGoalStatus>()

    init {
        val friends = SubscriptionManager.getFriendIds(Firebase.auth.currentUser!!)
        Log.v("INFO", ">>>>>>>\t\t\t\t${friends.toString()}")
        for (f in friends) {
            var friendDoc : DocumentSnapshot? = null
            runBlocking {
                friendDoc = Firebase.firestore.collection("users").document(f).get().await()
            }
            if (friendDoc != null) {
                var name = friendDoc!!.data?.get("name").toString()
                Log.v("INFO", ">>>>>>>\t\t\t\t$name ++++++ ${friendDoc!!.data?.toString()}")
                var goalsAchieved = friendDoc!!.data?.get("completedGoals") as Long
                var totalGoals = friendDoc!!.data?.get("totalGoals") as Long
                goalStatuses.add(FriendGoalStatus(name, goalsAchieved, totalGoals))
            }
        }
    }


    interface OnItemClickListener {
        fun onItemClick(imageNo: Int, name: String, goalRatioAchieved: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        val v = LayoutInflater.from(parent.context).inflate(R.layout.friends_list_item, parent, false)
        return ViewHolder(v)
    }


    override fun onBindViewHolder(holder: FriendsRecyclerAdapter.ViewHolder, i: Int) {
        val image : Int = if ((goalStatuses[i].goalsAchieved / goalStatuses[i].totalGoals) == 1L) {
            R.drawable.gold_trophy
        } else {
            R.drawable.silver_trophy

        }

        holder.trophy.setImageResource(image)
        holder.friendName.text = goalStatuses[i].friendName
        var goalString = "Achieved " + goalStatuses[i].goalsAchieved.toString() + "/" + goalStatuses[i].totalGoals.toString() + " of their goals!"
        holder.friendGoalRatioAchievedText.text = goalString
        holder.bind(image, goalStatuses[i].friendName, goalString, listener)
    }

    override fun getItemCount(): Int {
        return goalStatuses.size
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