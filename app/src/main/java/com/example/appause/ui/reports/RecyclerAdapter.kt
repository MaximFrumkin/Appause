package com.example.appause.ui.reports

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.appause.*

class RecyclerAdapter(mainActivity: MainActivity) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private val mainActivity: MainActivity = mainActivity
    private var context = mainActivity.applicationContext
    private var appTimer = AppTimer(context)

    private val description = arrayOf("Social", "Productivity", "Video", "Entertainment", "Movies")

    private val totalTime = arrayOf(2, 5, 2, 1, 1)

    // TODO use these in future

    // This list contains goalCategories and goalTime
    private var goals : List<Goal> = emptyList()
    // this variable represents total screen time
    private var totalTimeCurr: Long = 100
    // this represents usage for ith category
    private var goalTimeUsedCurr : List<Long> = listOf(2, 3, 1, 1, 0) as List<Long>

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun updateData() {
        // this is getting actual data
        // TODO fix the error on this line
        // appTimer.getCurrentUsage()
        var totalScreenTime = GoalTracker.totalTimeCurr

        // Dummy data
        val goal = Goal("", 0, listOf(), listOf())
        goal.goalName = "Total Screen Time"
        goal.goalTime = totalTimeCurr
        val newGoalList : MutableList<Goal> = mutableListOf(goal)
        for (i in description.indices) {
            val goal = Goal("", 0, listOf(), listOf())
            goal.goalName = description[i]
            goal.goalTime = totalTime[i].toLong()
            newGoalList.add(goal)
        }

        goals = newGoalList
        notifyDataSetChanged()
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

        val usage : String = if (goals[i].goalName == "Total Screen Time") {
            // set view for total screen time
            goals[i].goalTime.toString() + " h"
        } else {
            // set view for all other items
            val image = getViewImage(i)
            holder.hourglass.setImageResource(image)
            goalTimeUsedCurr[i-1].toString() + " / " + goals[i].goalTime.toString() + " h"
        }
        holder.usageTime.text = usage
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
    }
}
