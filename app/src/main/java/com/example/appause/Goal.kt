package com.example.appause

import android.app.usage.UsageEvents

/** TODO: add goal logic here and in [GoalTracker] and [AppTimer]
 */

//2 types of goals
//base don app itself (facebook etc)
//or base don app categories
//
class Goal(val goalName: String, var goalTime : Long, val appList: List<String>, val categoryList: List<String>) {
    fun eventIsInGoal(event : UsageEvents.Event): Boolean {
        return true
    }
}