package com.example.appause

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

class AppTimer(private val context: Context) {
    // TODO: trigger this whenever the user fetches the up to date info,
    //  and also trigger on a schedule at the end of the day to see if the goal has been achieved
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun getDailyUsage() {
        val yesterdayCal: Calendar = Calendar.getInstance()
        yesterdayCal.add(Calendar.DATE, -1)
        yesterdayCal.set(Calendar.HOUR_OF_DAY, 0)
        yesterdayCal.set(Calendar.MINUTE, 0)
        yesterdayCal.set(Calendar.SECOND, 0)
        val yesterday: Long = yesterdayCal.timeInMillis
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currTime = System.currentTimeMillis()
        val stats = usageStatsManager.queryEvents(yesterday, currTime)
        val eventsGroupedByApp : HashMap<String, MutableList<UsageEvents.Event>> = HashMap<String, MutableList <UsageEvents.Event>>()
        while (stats.hasNextEvent()) {
            val event = UsageEvents.Event()
            stats.getNextEvent(event)
            val appName : String = event.packageName
            if(appSwitch(event)) {
                if(eventsGroupedByApp[appName] == null){
                    eventsGroupedByApp[appName] = mutableListOf()
                }
                eventsGroupedByApp[appName]?.add(event)
            }
        }
        eventsGroupedByApp.forEach { appEvents ->
            // TODO: here we can look at which goals are relevant to this app
            //  and add the data to the usageDataGoals var of GoalTracker
            for(i in 0..appEvents.value.size - 2) {
                if (appEvents.value[i].eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    if (appEvents.value[i + 1].eventType == UsageEvents.Event.ACTIVITY_PAUSED) {
                        if (GoalTracker.usageDataAll[appEvents.key] == null) {
                            GoalTracker.usageDataAll[appEvents.key] = AppData()
                        }
                        val timeUsedCurr: Long =
                            appEvents.value[i + 1].timeStamp - appEvents.value[i].timeStamp
                        GoalTracker.usageDataAll[appEvents.key]?.timeUsed =
                            GoalTracker.usageDataAll[appEvents.key]?.timeUsed?.plus(
                                timeUsedCurr
                            )!!
                    }
                }
                if (appEvents.value[appEvents.value.size - 1].eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    val timeUsedCurr: Long = currTime - appEvents.value[appEvents.value.size - 1].timeStamp
                    GoalTracker.usageDataAll[appEvents.key]?.timeUsed =
                        GoalTracker.usageDataAll[appEvents.key]?.timeUsed?.plus(
                            timeUsedCurr
                        )!!
                }
                if (appEvents.value[0].eventType == UsageEvents.Event.ACTIVITY_PAUSED) {
                    val timeUsedCurr: Long =  appEvents.value[appEvents.value.size - 1].timeStamp - yesterday
                    GoalTracker.usageDataAll[appEvents.key]?.timeUsed =
                        GoalTracker.usageDataAll[appEvents.key]?.timeUsed?.plus(
                            timeUsedCurr
                        )!!
                }
            }
        }
    }
    private fun appSwitch(usageEvent: UsageEvents.Event): Boolean {
        return when (usageEvent.eventType) {
            UsageEvents.Event.ACTIVITY_PAUSED -> true
            UsageEvents.Event.ACTIVITY_RESUMED -> true
            else -> false
        }
    }
}