package com.example.appause

import android.app.Activity
import android.content.Intent
import android.media.MediaCodec.QueueRequest
import android.os.Bundle
import android.util.Log
import com.example.appause.expview.OnboardingActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.*

/**
 * Adapted from https://firebase.google.com/docs/auth/android/google-signin
 */
class SignInActivity : Activity() {
    private var MOCKED_TOTAL_GOALS: Int = 0
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var firstTimeUser : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, ">ONCREATE")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.SERVER_CLIENT_ID)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun updateUI(user: FirebaseUser?) {
        Log.d(TAG, "UPDATING UI BASED ON $user")
        if (user != null) {
            // Signed in!
            CurrentUser.user = user
            checkIfUserExists(user)
            Log.v("SIGNING IN", "USER>>>$user WITH FIRST TIME BEING $firstTimeUser")
            val myIntent = if (firstTimeUser) Intent(this@SignInActivity, OnboardingActivity::class.java) else Intent(this@SignInActivity, MainActivity::class.java)
            Log.v("SIGNING IN", "USER>>>$user GOING TO $myIntent")
            this@SignInActivity.startActivity(myIntent)
        } else {
            signIn()
        }
    }

    private fun addUser(user: FirebaseUser) {
        val db = Firebase.firestore
        // Create a new user with a first and last name
        var userProfile = hashMapOf(
            "email" to (user.email?.lowercase(Locale.getDefault()) ?: null),
            "name" to (user.displayName?.lowercase(Locale.getDefault()) ?: null),
            "friendRequests" to emptyList<String>(),
            "totalGoals" to MOCKED_TOTAL_GOALS,
            "completedGoals" to 0
        )

        val TAG = "MyActivity"

        runBlocking {
            db.collection("users")
                .add(userProfile).await()
        }
    }

    private fun checkIfUserExists(user: FirebaseUser) {
        val db = Firebase.firestore
        val TAG = "MyActivity"

        val usersRef = db.collection("users")
        var checkTask : QuerySnapshot? = null
        runBlocking {
            checkTask = usersRef.whereEqualTo("email", user.email)
                .get().await()
        }

        if (checkTask == null) {
            addUser(user)
        } else {
            Log.v("SIGINACTIVITY", "CHECK TASK COMPLETED WITH RESULT ${checkTask!!.documents}")
            if (checkTask!!.documents.isEmpty()) {
                addUser(user)
            } else {
                firstTimeUser = false
            }
        }
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}
