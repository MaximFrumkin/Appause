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
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.appause.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

fun getUserDocId(user: FirebaseUser): Int {
    val db = Firebase.firestore
    val TAG = "MainActivity"
    val usersRef = db.collection("users")
    var id = -1
    usersRef.whereEqualTo("email", user.email)
        .get()
        .addOnSuccessListener { documents ->
            assert(documents.size() == 1)

            for (doc in documents) {
                val friendsList = doc.id as Int
            }
        }
        .addOnFailureListener { exception ->
            Log.w(TAG, "Error getting documents: ", exception)
        }

    return id
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStart() {
        super.onStart()
        handlePermissions()
    }

    val goalTracker: GoalTracker = GoalTracker
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
        val providers: List<AuthUI.IdpConfig> = listOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )


        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)

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



        AppauseNotificationManager(applicationContext).send()
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
            //TODO send the info about goals achieved
            // from the GoalTracker.numAchievedGoalsYesterday
            // and the total number of goals to firestore
            if (GoalTracker.isMilestone()) {
                //TODO: notify friends about milestone
                val user = FirebaseAuth.getInstance().currentUser!!
                CurrentUser.user = user
                updateUsersMileStoneField(user, GoalTracker.goalStreakDays)
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

    /*override fun onResume() {
        super.onResume()
        appTimer?.getCurrentUsage()
        print(goalTracker.totalTimeCurr)
    }*/
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        Log.v("INFO", ">>>>>>>\t\t\t\tGOT THE RESPONSE: " + response.toString())
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in. Non-null asserted because result code is not an error.
            val user = FirebaseAuth.getInstance().currentUser!!
            CurrentUser.user = user
            Log.v("INFO", ">>>>>>>\t\t\t\tUSER: " + user.toString())
            if (user != null) {
                Toast.makeText(
                    this@MainActivity,
                    "Welcome back to Appause " + user.displayName + "!",
                    Toast.LENGTH_SHORT
                ).show()
                checkIfUserExists(user)
            }
        } else {

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
                val signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(
                        listOf(
                            AuthUI.IdpConfig.GoogleBuilder().build()
                        )
                    )
                    .build()
                signInLauncher.launch(signInIntent)
            }

            builder.setNegativeButton("No, close the app") { dialog, _ ->
                dialog.dismiss()
                this.finish()
                exitProcess(0)
            }
            builder.setMessage("Welcome to Appause! Please sign in to get started.")
            builder.show()


        }
    }


    private fun addUser(user: FirebaseUser) {
        val db = Firebase.firestore
        // Create a new user with a first and last name
        var userProfile = hashMapOf(
            "email" to user.email,
            "name" to user.displayName,
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




    private fun updateUsersMileStoneField(user: FirebaseUser, mileStone: Int) {
        val db = Firebase.firestore
        val TAG = "MainActivity"
        val userDocId = getUserDocId(user)
        val userRef = db.collection("users").document("$userDocId")
        userRef.update("milestone", mileStone)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
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