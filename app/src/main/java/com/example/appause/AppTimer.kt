package com.example.appause

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

class AppTimer(private val context: Context) {
    val pm: PackageManager = context.getPackageManager();

    // TODO: trigger getCurrentUsage() whenever the user fetches the up to date info
    /**
     *  Get the usage data from midnight to the current time, and store it in the [GoalTracker] object.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun getCurrentUsage(){
        val midnightCal: Calendar = Calendar.getInstance()
        midnightCal.set(Calendar.HOUR_OF_DAY, 0)
        midnightCal.set(Calendar.MINUTE, 0)
        midnightCal.set(Calendar.SECOND, 0)
        val midnight: Long = midnightCal.timeInMillis
        val currTime = System.currentTimeMillis()
        GoalTracker.usageDataAllCurr = HashMap<String, AppData>()//reset the usageDataAllCurr
        getUsage(midnight, currTime, false)
    }
    // TODO: trigger getDailyUsage() on a schedule at the end of the day to see if the goal has been achieved
    /**
     *  Get the usage data for the past day, and store it in the [GoalTracker] object.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun getDailyUsage(){
        val yesterdayCal: Calendar = Calendar.getInstance()

        yesterdayCal.set(Calendar.HOUR_OF_DAY, 0)
        yesterdayCal.set(Calendar.MINUTE, 0)
        yesterdayCal.set(Calendar.SECOND, 0)

        val yesterdayMidnight: Long = yesterdayCal.timeInMillis
        yesterdayCal.add(Calendar.DATE, -1)
        val yesterdayMorning: Long = yesterdayCal.timeInMillis
        GoalTracker.usageDataAllYesterday = HashMap<String, AppData>()//reset the usageDataAllYesterday
        getUsage(yesterdayMorning, yesterdayMidnight, true)
    }

    /**
     *  Get the usage data within a certain timeframe, and store it in the [GoalTracker] object.
     *
     *  Parameters:
     *
     *  [begin]
     *      - The beginning time for the app usage data. Events before this time will not be included.
     *
     *  [end]
     *      - The end time for the app usage data. Events after this time will not be included.
     *
     *  [isDaily]
     *      - Whether or not the usage is for the daily app data. This impacts how it is stored in the GoalTracker object.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun getUsage(begin : Long, end : Long, isDaily : Boolean) {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val stats = usageStatsManager.queryEvents(begin, end)
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
            var applicationInfo: ApplicationInfo = pm.getApplicationInfo(appEvents.key, 0)
            var appCategory: Int = 0
            var categoryTitle: String = ""
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                appCategory = applicationInfo.category
                categoryTitle = ApplicationInfo.getCategoryTitle(context, appCategory) as String
            }
            // TODO: here we can look at which goals are relevant to this app
            //  and add the data to GoalTracker
            for(i in 0..appEvents.value.size - 2) {
                if (appEvents.value[i].eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    if (appEvents.value[i + 1].eventType == UsageEvents.Event.ACTIVITY_PAUSED) {
                        GoalTracker.initUsageDataKey(appEvents.key, isDaily)
                        val timeUsedCurr: Long =
                            appEvents.value[i + 1].timeStamp - appEvents.value[i].timeStamp
                        GoalTracker.updateUsageDataAll(appEvents.key, categoryTitle, timeUsedCurr, isDaily)
                    }
                }
                if (appEvents.value[appEvents.value.size - 1].eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    val timeUsedCurr: Long = end - appEvents.value[appEvents.value.size - 1].timeStamp
                    GoalTracker.updateUsageDataAll(appEvents.key, categoryTitle, timeUsedCurr, isDaily)
                }
                if (appEvents.value[0].eventType == UsageEvents.Event.ACTIVITY_PAUSED) {
                    val timeUsedCurr: Long =  appEvents.value[appEvents.value.size - 1].timeStamp - begin
                    GoalTracker.updateUsageDataAll(appEvents.key, categoryTitle, timeUsedCurr, isDaily)
                }
            }
        }
    }

    /**
     * Checks if the event results in the change of the currently active application
     */
    private fun appSwitch(usageEvent: UsageEvents.Event): Boolean {
        return when (usageEvent.eventType) {
            UsageEvents.Event.ACTIVITY_PAUSED -> true
            UsageEvents.Event.ACTIVITY_RESUMED -> true
            else -> false
        }
    }
}