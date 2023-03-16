package com.example.appause

import com.google.firebase.auth.FirebaseUser
import java.util.HashMap

/**
 * Holds who the current user is
*/
object CurrentUser {
    lateinit var user : FirebaseUser
}