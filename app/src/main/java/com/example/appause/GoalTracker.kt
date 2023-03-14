package com.example.appause

import java.util.HashMap

/**
 * Tracks data relating to the overall usage data, goals, and usage data relevant to each goal
 *
 * [goals]
 * List of the goals
 *
 * [goalAppsYesterday]
 * List of the app names relevant to each goal. Only apps used yesterday are included.
 * For index i in the list, the list of app names are only the apps relevant to the goal at index i of [goals].
 * The app data can be be accessed by calling [usageDataAllYesterday] [app name].
 * This saves space as compared to storing the usage data hashmap for each goal,
 * as the apps that pertain to multiple goals will not have their app data stored multiple times.
 *
 * [goalAppsCurr]
 * List of the app names relevant to each goal. Only apps used from midnight to the current time are included.
 * For index i in the list, the list of app names are only the apps relevant to the goal at index i of [goals].
 * The app data can be be accessed by calling [usageDataAllCurr] [app name].
 * This saves space as compared to storing the usage data hashmap for each goal,
 * as the apps that pertain to multiple goals will not have their app data stored multiple times.
 *
 * [usageDataAllYesterday]
 * HashMap of all app usage data. The usage data is from yesterday.
 *
 * [usageDataAllCurr]
 * HashMap of all app usage data. The usage data is from midnight to the current time.
 */
object  GoalTracker {
    var goals : MutableList<Goal> = mutableListOf()
    val goalAppsYesterday : MutableList<MutableList<String>> = mutableListOf()
    val goalAppsCurr : MutableList<MutableList<String>> = mutableListOf()
    var usageDataAllYesterday :  HashMap<String, AppData> = HashMap<String, AppData>()
    var goalTimeUsedYesterday: List<Long> = emptyList()
    var goalTimeUsedCurr: List<Long> = emptyList()
    var totalTimeYesterday: Long = 0
    var totalTimeCurr: Long = 0
    var usageDataAllCurr :  HashMap<String, AppData> = HashMap<String, AppData>()
    fun updateUsageDataAll(key: String, category: String, timeUsedCurr : Long, isDaily : Boolean){
        if(isDaily) {
            usageDataAllYesterday[key]?.timeUsed =
                usageDataAllYesterday[key]?.timeUsed?.plus(
                    timeUsedCurr
                )!!
            for(i in goals.indices) {
                if (category in goals[i].categoryList || key in goals[i].appList) {//pretty sure i can just have a map of categories -> list of goals but whatever
                    goalTimeUsedCurr[i]?.plus(timeUsedCurr)
                }
            }
            totalTimeCurr.plus(timeUsedCurr)
        } else {
            usageDataAllCurr[key]?.timeUsed =
                usageDataAllCurr[key]?.timeUsed?.plus(
                    timeUsedCurr
                )!!
            for(i in goals.indices) {
                if (category in goals[i].categoryList || key in goals[i].appList) {//pretty sure i can just have a map of categories -> list of goals but whatever
                    goalTimeUsedYesterday[i]?.plus(timeUsedCurr)
                }
            }
            totalTimeYesterday.plus(timeUsedCurr)
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
    fun addGoal(name: String, time: Long, apps: List<String>, categories: List<String>) {
        val goal = Goal(name, time, apps, categories)
        goals.add(goal)
        print("addGoal finished")
    }
}