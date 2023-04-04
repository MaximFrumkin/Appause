package com.example.appause

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class UserProfile {
    var name : String = ""
    var email : String = ""

    // Normalizes the text to all lowercase
    constructor(name: String, email: String) {
        this.name = name.lowercase(Locale.getDefault())
        this.email = email.lowercase(Locale.getDefault())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true // checks if the objects are the same instance
        if (other == null || other !is UserProfile) return false // checks if the other object is null or of a different type

        return this.name == other.name && this.email == other.email // checks if the name and age properties are the same
    }
}