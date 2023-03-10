package com.example.appause

import java.util.HashMap

/**
 * Tracks data relating to the overall usage data, goals, and usage data relevant to each goal
 *
 * [goals]
 * List of the goals
 *
 * [usageDataGoalsYesterday]
 * List of the usage data relevant to each goal. The usage data is from yesterday.
 * For index i in the list, the app data hashmap is only the data relevant to the goal at index i of [goals].
 *
 * [usageDataGoalsCurr]
 * List of the usage data relevant to each goal. The usage data is from midnight to the current time.
 * For index i in the list, the app data hashmap is only the data relevant to the goal at index i of [goals].
 *
 * [usageDataAllYesterday]
 * HashMap of all app usage data. The usage data is from yesterday.
 *
 * [usageDataAllCurr]
 * HashMap of all app usage data. The usage data is from midnight to the current time.
 */
object  GoalTracker {
    val goals : List<String> = emptyList()
    val usageDataGoalsYesterday : List<HashMap<String, AppData>> = emptyList()
    val usageDataGoalsCurr : List<HashMap<String, AppData>> = emptyList()
    var usageDataAllYesterday :  HashMap<String, AppData> = HashMap<String, AppData>()
    var usageDataAllCurr :  HashMap<String, AppData> = HashMap<String, AppData>()
    fun updateUsageDataAll(key: String, timeUsedCurr : Long, isDaily : Boolean){
        if(isDaily) {
            usageDataAllYesterday[key]?.timeUsed =
                usageDataAllYesterday[key]?.timeUsed?.plus(
                    timeUsedCurr
                )!!
        } else {
            usageDataAllCurr[key]?.timeUsed =
                usageDataAllCurr[key]?.timeUsed?.plus(
                    timeUsedCurr
                )!!
        }
    }
    fun initUsageDataKey(key: String, isDaily : Boolean){
        if(isDaily) {
            if (usageDataAllYesterday[key] == null) {
                usageDataAllYesterday[key] = AppData()
            }
        } else {
            if (usageDataAllCurr[key] == null) {
                usageDataAllCurr[key] = AppData()
            }
        }
    }
}