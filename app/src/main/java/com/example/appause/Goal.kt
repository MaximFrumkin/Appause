package com.example.appause

import android.app.usage.UsageEvents

/** TODO: add goal logic here and in [GoalTracker] and [AppTimer]
 */

//2 types of goals
//base don app itself (facebook etc)
//or base don app categories
//
class Goal {
    var goalName : String = ""
    var goalTime : Long = 0
    val categoryList: MutableSet<String> = mutableSetOf()
    val appList: List<String> = emptyList()
    fun eventIsInGoal(event : UsageEvents.Event): Boolean {
        return true
    }
}