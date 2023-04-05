package com.example.appause

import android.widget.EditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.HashMap
import kotlinx.serialization.Serializable

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
@Serializable
object GoalTracker {
    var goals : MutableList<Goal> = mutableListOf()
    //val goalAppsYesterday : MutableList<MutableList<String>> = mutableListOf()
    //val goalAppsCurr : MutableList<MutableList<String>> = mutableListOf()
    var usageDataAllYesterday :  HashMap<String, AppData> = HashMap<String, AppData>()
    var goalTimeUsedYesterday: MutableList<Long> = mutableListOf()
    var goalTimeAllowed: MutableList<Long> = mutableListOf()
    var goalTimeUsedCurr: MutableList<Long> = mutableListOf()
    var numAchievedGoalsYesterday = 0
    var goalStreakDays = 0;
    var totalTimeYesterday: Long = 0
    var totalTimeCurr: Long = 0
    var usageDataAllCurr :  HashMap<String, AppData> = HashMap<String, AppData>()

    init {
        goals = goals.toMutableList()
    }
    fun updateUsageDataAll(key: String, category: String, timeUsedCurr : Long, isDaily : Boolean) {
        if(isDaily) {
            usageDataAllYesterday[key]?.timeUsed =
                usageDataAllYesterday[key]?.timeUsed?.plus(
                    timeUsedCurr
                )!!
            for(i in goals.indices) {
                if (category in goals[i].categoryList || key in goals[i].appList) {//pretty sure i can just have a map of categories -> list of goals but whatever
                    goalTimeUsedYesterday[i]?.plus(timeUsedCurr)
                }
            }
            totalTimeYesterday.plus(timeUsedCurr)
        } else {
            usageDataAllCurr[key]?.timeUsed =
                usageDataAllCurr[key]?.timeUsed?.plus(
                    timeUsedCurr
                )!!
            for(i in goals.indices) {
                if (category in goals[i].categoryList || key in goals[i].appList) {//pretty sure i can just have a map of categories -> list of goals but whatever
                    goalTimeUsedCurr[i]?.plus(timeUsedCurr)
                }
            }
            totalTimeCurr.plus(timeUsedCurr)
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

    fun reset() {
        goals.clear() //mutableListOf()
        goalTimeUsedYesterday.clear()
        goalTimeAllowed.clear()
        goalTimeUsedCurr.clear()
        numAchievedGoalsYesterday = 0
    }

    fun addGoal(name: String, time: Long, apps: List<String>, categories: List<String>) {
        goalTimeUsedYesterday.add(0)
        goalTimeUsedCurr.add(0)
        goalTimeAllowed.add(0)
        val goal = Goal(name, time, apps, categories)
        goals.add(goal)
        print("addGoal finished")
    }

    fun isEmpty(): Boolean {
        return goals.size == 0 && usageDataAllCurr.isEmpty() && usageDataAllYesterday.isEmpty()
    }

    fun countGoals(){
        numAchievedGoalsYesterday = 0;
        for (i in goalTimeUsedYesterday.indices){
            if(goalTimeAllowed[i] >= goalTimeUsedYesterday[i]){
                numAchievedGoalsYesterday++;
            }
        }
        if(numAchievedGoalsYesterday == goalTimeAllowed.size){
            goalStreakDays++
        }else {
            goalStreakDays = 0;
        }

        Firebase.firestore.collection("users").document(getUserDocIdBlocking(CurrentUser.user))
            .update(
                mutableMapOf(
                    "goalsCompleted" to numAchievedGoalsYesterday
                ) as Map<String, Any>
            )
    }

    fun isMilestone(): Boolean {
        if (goalStreakDays == 0) {
            return false;
        } else if(goalStreakDays == 1) {
            return true;
        } else return goalStreakDays % 10 == 0
    }
}