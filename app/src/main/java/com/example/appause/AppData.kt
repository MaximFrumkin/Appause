package com.example.appause

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
class AppData : Parcelable {
    var timeUsed : Long = 0
}