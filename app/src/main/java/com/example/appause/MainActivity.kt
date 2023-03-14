package com.example.appause

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.appause.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var user: FirebaseUser

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    val goalTracker: GoalTracker = GoalTracker
    var appTimer: AppTimer? = null
    val apps: List<String> = listOf("com.google.android.youtube")
    val categories: List<String> = emptyList()

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
        // TODO: currently we always launch this permission activity
        //  instead of immediately launching we should check if the permission has been granted
        //  and if it hasn't then launch a dialog where if the user presses ok we launch the activity
        startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
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
            user = FirebaseAuth.getInstance().currentUser!!
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
            Toast.makeText(
                this@MainActivity,
                "Welcome to Appause! Please sign in to get started.",
                Toast.LENGTH_SHORT
            ).show()
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