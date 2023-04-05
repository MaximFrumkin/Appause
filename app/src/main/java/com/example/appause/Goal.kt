package com.example.appause

import android.app.usage.UsageEvents
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


/** TODO: add goal logic here and in [GoalTracker] and [AppTimer]
 */

//2 types of goals
//base don app itself (facebook etc)
//or base don app categories
//
@Serializable
@Parcelize
class Goal(var goalName: String, var goalTime : Long, val appList: List<String>, val categoryList: List<String>) :
    Parcelable {
    fun eventIsInGoal(event : UsageEvents.Event): Boolean {
        return true
    }
}