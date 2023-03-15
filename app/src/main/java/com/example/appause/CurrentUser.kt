package com.example.appause

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.HashMap

/**
 * Holds who the current user is
*/
object CurrentUser {
    lateinit var user : FirebaseUser

    fun getUsersId(email: String): Task<String?> {
        val db = Firebase.firestore

        val docRef = db.collection("users")
        val query = docRef.whereEqualTo("email", email).limit(1)

        return query.get().continueWith { task ->
            if (task.isSuccessful) {
                val result = task.result
                if (result != null && !result.isEmpty) {
                    result.documents[0].id
                } else {
                    null
                }
            } else {
                Log.e("Firebase", "Error getting id")
                null
            }
        }
    }
}