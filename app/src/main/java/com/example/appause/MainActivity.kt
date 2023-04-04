package com.example.appause

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.AppOpsManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.appause.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

fun getUserDocIdBlocking(user: FirebaseUser): String {
    val db = Firebase.firestore
    val usersRef = db.collection("users")
    var id = "DUMMY VALUE"
    var idTask: QuerySnapshot? = null
    runBlocking {
        idTask = usersRef.whereEqualTo("email", user.email)
            .get().await()
    }
    if (idTask != null) {
        id = idTask!!.documents[0].id
    }
    return id
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStart() {
        super.onStart()
        handlePermissions()
    }

    val goalTracker: GoalTracker = GoalTracker
    lateinit var mileStoneCommunicationManager: MileStoneCommunicationManager
    var appTimer: AppTimer? = null
    val apps: List<String> = listOf("com.google.android.youtube")
    val categories: List<String> = emptyList()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.v("INFO", ">>>>>>>\t\t\t\tHELLO WORLD")
        // Choose authentication providers
        appTimer = AppTimer(this.applicationContext)
        goalTracker.addGoal("youtube", 6000, apps, categories)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_friends
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        setAlarmTomorrow()
        mileStoneCommunicationManager = MileStoneCommunicationManager(applicationContext)
        SubscriptionManager.ensureSubscribedToFriends(applicationContext)

        // If the user's document changes, it could be a new friend being added so we
        // refresh our subscriptions.
        Firebase.firestore.collection("users")
            .document(getUserDocIdBlocking(FirebaseAuth.getInstance().currentUser!!))
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("FIREBASE_LISTENER", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    SubscriptionManager.ensureSubscribedToFriends(applicationContext)
                }
            }

        if (intent.hasExtra("milestone")) {
            val milestone = intent.getIntExtra("milestone", 0)
            val friendId = intent.getStringExtra("friendid")
            val friendName = intent.getStringExtra("friendname")

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
                Log.d("CONGRATS", "SENT OUT CONGRATULATIONS")
                // Append to friends milestone congratulations list
                if (friendId != null) {
                    Firebase.firestore.collection("users").document(friendId).update(
                        mutableMapOf(
                            "congratulators" to FieldValue.arrayUnion(friendName)
                        ) as Map<String, Any>
                    )
                }
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.setMessage(
                "Congratulate $friendName on the $milestone-day streak?"
            )

            builder.show()
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun dailyGoalCheck() {
        if (appTimer != null) {
            appTimer!!.getDailyUsage()
            GoalTracker.countGoals()
            //schedule new alarm for tomorrow. This is necessary as setRepeating() is inexact
            // after android API 19 to save battery,
            // so in order to guarantee that the daily goal check happens within 20 minutes of midnight,
            // we use a setWindow instead, and set it each day
            setAlarmTomorrow()
            if (GoalTracker.isMilestone()) {
                mileStoneCommunicationManager.updateFriendsOnMileStone(GoalTracker.goalStreakDays)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarmTomorrow() {
        val broadcastReceiver = object : BroadcastReceiver() {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    "CountGoals" -> dailyGoalCheck()
                }
            }
        }
        val goalIntent = Intent(applicationContext, broadcastReceiver::class.java)
        goalIntent.action = "CountGoals"
        val alarmIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            goalIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        val tomorrowCal: Calendar = Calendar.getInstance()
        tomorrowCal.set(Calendar.HOUR_OF_DAY, 0)
        tomorrowCal.set(Calendar.MINUTE, 0)
        tomorrowCal.set(Calendar.SECOND, 0)
        tomorrowCal.add(Calendar.DATE, 1)
        val tomorrowMidnight: Long = tomorrowCal.timeInMillis
        alarmManager.setWindow(
            AlarmManager.RTC,
            tomorrowMidnight,
            TimeUnit.MINUTES.toMillis(20),
            alarmIntent
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun handlePermissions() {
        @RequiresApi(Build.VERSION_CODES.Q)
        fun getUsageAccessGranted(): Boolean {
            return try {
                val packageManager: PackageManager = this.applicationContext.packageManager
                val applicationInfo =
                    packageManager.getApplicationInfo(this.applicationContext.packageName, 0)
                val appOpsManager =
                    this.applicationContext.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val mode = appOpsManager.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid,
                    applicationInfo.packageName
                )
                (mode == AppOpsManager.MODE_ALLOWED)
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }
        if (!getUsageAccessGranted()) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
                startActivity(Intent(ACTION_USAGE_ACCESS_SETTINGS))
            }

            builder.setNegativeButton("No, close the app") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(
                    this@MainActivity,
                    "In order to user Appause please grant the requested permissions.",
                    Toast.LENGTH_LONG
                ).show()

                this.finish()
                exitProcess(0)
            }
            builder.setMessage(
                "Appause requires your app usage data in order to be able " +
                        "to track your progress in completing your goals!" +
                        " This data will not leave your device!"
            )
            builder.show()
        }
    }

    override fun onResume() {
        super.onResume()
        appTimer?.getCurrentUsage()
        Log.v("INFO", ">>>>>>>\t\t\t\t${goalTracker.totalTimeCurr}")
    }

    private fun addUser(user: FirebaseUser) {
        val db = Firebase.firestore
        // Create a new user with a first and last name
        var userProfile = hashMapOf(
            "email" to (user.email?.lowercase(Locale.getDefault()) ?: null),
            "name" to (user.displayName?.lowercase(Locale.getDefault()) ?: null),
            "friendRequests" to emptyList<String>()
        )

        val TAG = "MyActivity"

        db.collection("users")
            .add(userProfile)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "User added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding user", e)
            }
    }

    private fun checkIfUserExists(user: FirebaseUser) {
        val db = Firebase.firestore
        val TAG = "MyActivity"

        val usersRef = db.collection("users")
        usersRef.whereEqualTo("email", user.email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.size() == 0) {
                    addUser(user)
                }

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun dbMethod() {
        val db = Firebase.firestore
        // Create a new user with a first and last name
        var user = hashMapOf(
            "first" to "Ada",
            "last" to "Lovelace",
            "born" to 1815
        )

        val TAG = "MyActivity"

// Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }


        // Create a new user with a first, middle, and last name
        user = hashMapOf(
            "first" to "Alan",
            "middle" to "Mathison",
            "last" to "Turing",
            "born" to 1912
        )

// Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "DATABASE OUTPUT - ${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

    }


}