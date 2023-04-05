package com.example.appause

import android.app.usage.UsageEvents
import kotlinx.serialization.Serializable


/** TODO: add goal logic here and in [GoalTracker] and [AppTimer]
 */

//2 types of goals
//base don app itself (facebook etc)
//or base don app categories
//
@Serializable
class Goal(var goalName: String, var goalTime : Long, val appList: List<String>, val categoryList: List<String>) {
    fun eventIsInGoal(event : UsageEvents.Event): Boolean {
        return true
    }
}