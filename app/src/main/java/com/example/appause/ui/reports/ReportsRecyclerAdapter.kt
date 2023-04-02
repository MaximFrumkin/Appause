package com.example.appause.ui.reports

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.appause.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class ReportsRecyclerAdapter(mainActivity: MainActivity, private val listener: OnItemClickListener) : RecyclerView.Adapter<ReportsRecyclerAdapter.ViewHolder>() {
    private val mainActivity: MainActivity = mainActivity
    private var context = mainActivity.applicationContext
    private var appTimer = AppTimer(context)

    // This list contains goalCategories and goalTime
    private var goals : List<Goal> = emptyList()
    // this variable represents total screen time
    private var totalScreenTime: Long = 0
    // this represents usage for ith category
    private var goalTimeUsedCurr : List<Long> = emptyList()
    // This represents the list of people who congratulated the current user.
    private var congratulators : List<String> = emptyList()
    // Congratulations message
    private val congratulationsMessage = "Take a bow!"

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun updateData() {
        // this is getting actual data
//        appTimer.getCurrentUsage()

        // TODO remove this block later
        /////////////////////////////////////////////////////////////////////////////////////////////
        // Fill in GoalTracker object with dummy data and use that dummy data. TODO Later, do not fill the object
        GoalTracker.totalTimeCurr = 7
        GoalTracker.goalTimeUsedCurr = listOf(2, 3, 1, 1, 0)
        val goal1 = Goal("Social", 2, listOf(), listOf())
        val goal2 = Goal("Productivity", 5, listOf(), listOf())
        val goal3 = Goal("Video", 2, listOf(), listOf())
        val goal4 = Goal("Entertainment", 1, listOf(), listOf())
        val goal5 = Goal("Movies", 1, listOf(), listOf())
        GoalTracker.goals = listOf(goal1, goal2, goal3, goal4, goal5) as MutableList<Goal>
        /////////////////////////////////////////////////////////////////////////////////////////////


        val totalScreenTimeGoal = Goal("", 0, listOf(), listOf())
        totalScreenTimeGoal.goalName = "Total Screen Time"

        ///////////////////
        // CONGRATULATORS LOGIC
        ///////////////////
        val db = Firebase.firestore
        val usersRef = db.collection("users")
        var congratulatorsTask : QuerySnapshot? = null
        runBlocking {
            congratulatorsTask = usersRef.whereEqualTo("email", FirebaseAuth.getInstance().currentUser!!.email)
                .get().await()
        }
        if (congratulatorsTask != null) {
            congratulators = congratulatorsTask!!.documents[0].data?.get("congratulators") as List<String>
        }

        ///////////////////

        val newGoalList : MutableList<Goal> = mutableListOf()

        if (congratulators.isNotEmpty()) {
            val congratulatorsGoal = Goal(congratulationsMessage, 0, congratulators, listOf())
            newGoalList.add(congratulatorsGoal)
        }

        newGoalList.add(totalScreenTimeGoal)

        for (goal in GoalTracker.goals) {
            newGoalList.add(goal)
        }
        goals = newGoalList
        totalScreenTime = GoalTracker.totalTimeCurr
        goalTimeUsedCurr = GoalTracker.goalTimeUsedCurr

        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(goal: Goal)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.reports_list_item, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, i: Int) {


        holder.description.text = goals[i].goalName

        if (goals[i].goalName == "Total Screen Time") {
            // set view for total screen time

            // set image for for total screen time
            // todo set total screen time image here
            val image = R.drawable.screen_time
            holder.hourglass.setImageResource(image)

            val usage = "$totalScreenTime h"
            holder.usageTime.text = usage

        } else if (goals[i].goalName == congratulationsMessage) {
            // set view for total screen time

            // set image for for total screen time
            // todo set total screen time image here
            val image = R.drawable.applause
            holder.hourglass.setImageResource(image)

            val usage = if (congratulators.size == 1)  "${congratulators[0]} applauded your latest streak!" else "${congratulators[0]} and ${congratulators.size - 1} more applauded your latest streak!"
            holder.usageTime.text = usage

        } else {
            // set view for all other items
            val image = getViewImage(i)
            holder.hourglass.setImageResource(image)

            val usage : String = goalTimeUsedCurr[i-1].toString() + " / " + goals[i].goalTime.toString() + " h"
            holder.usageTime.text = usage

            holder.bind(goals[i], listener)
        }
    }

    override fun getItemCount(): Int {
        return goals.size
    }

    private fun getViewImage(position: Int): Int {

        val  timeUsed = goalTimeUsedCurr[position-1]

        // TODO: implement logic that if 50% used, then glass half full etc then implement actual data logic
        val totalTime = goals[position].goalTime

        val image : Int = if (totalTime == timeUsed) {
            R.drawable.hourglass_bottom
        } else if (timeUsed == 0L) {
            R.drawable.hourglass_top
        } else {
            R.drawable.hourglass
        }
        return image
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var hourglass: ImageView
        var description: TextView
        var usageTime: TextView

        init {
            hourglass = itemView.findViewById(R.id.trophy)
            description = itemView.findViewById(R.id.friendName)
            usageTime = itemView.findViewById(R.id.friendGoalRatioAchievedText)
        }

        fun bind(goal: Goal, listener: OnItemClickListener) {
            val view = itemView.findViewById<CardView>(R.id.report_card)
            view.setOnClickListener {
                Log.d("Reports", "listener is set: $goal")
                listener.onItemClick(goal)
            }
        }
    }

}
