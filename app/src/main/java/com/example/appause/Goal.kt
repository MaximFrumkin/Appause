package com.example.appause

import android.app.usage.UsageEvents

/** TODO: add goal logic here and in [GoalTracker] and [AppTimer]
 */
class Goal {
    val goalName : String = ""

    fun eventIsInGoal(event : UsageEvents.Event): Boolean {
        return true
    }
}