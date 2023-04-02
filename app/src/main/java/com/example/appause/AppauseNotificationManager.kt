package com.example.appause

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.firebase.ui.auth.AuthUI.getApplicationContext
import org.json.JSONException
import org.json.JSONObject

// ADAPTED FROM: https://medium.com/@mendhie/send-device-to-device-push-notifications-without-server-side-code-238611c143

class AppauseNotificationManager constructor(context: Context, topic: String, title: String, message: String) {
    private var NOTIFICATION_TITLE = title
    private var NOTIFICATION_MESSAGE = message
    private val TOPIC = "/topics/$topic"
    private val TAG = "NetworkManager"
    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey = "key=" + "AAAADTI-dZA:APA91bH8WAJ5sXI1zCyfFhDjdSS3ZgMWhsnC3S_vND7kW3Q2YYEj48k6NZ4a8AzFgU61RFhPoyyBKInmhFFB0K68Oh0dSGvgHWCew7eEqqSDYzaeNd1zR6ZEx5TazodqEhWAB3zfzPg0"
    private val contentType = "application/json"
    private val ctx: Context

    init {
        ctx = context
    }

    @SuppressLint("RestrictedApi")
    private fun sendNotification(notification: JSONObject) {
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject?> { response -> Log.i(TAG, "onResponse: $response") },
            Response.ErrorListener {
                Toast.makeText(ctx, "Request error", Toast.LENGTH_LONG).show()
                Log.i(TAG, "Fail -> $it")
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Authorization"] = serverKey
                    params["Content-Type"] = contentType
                    return params
                }
        }
        MySingleton.getInstance(ctx)?.addToRequestQueue(jsonObjectRequest)
    }

    fun send(milestone : Int?, userid : String?, friendName : String?) {
        val notification = JSONObject()
        val notifcationBody = JSONObject()
        val dataBody = JSONObject()
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE)
            notifcationBody.put("message", NOTIFICATION_MESSAGE)
            notification.put("to", TOPIC)
            notification.put("notification", notifcationBody)
            if (milestone != null) {
                dataBody.put("milestone", milestone)
                dataBody.put("friendid", userid)
                dataBody.put("friendname", friendName)
            }
            notification.put("data", dataBody)
        } catch (e: JSONException) {
            Log.e(TAG, "onCreate: " + e.message)
        }
        sendNotification(notification)
    }
}

class MySingleton private constructor(context: Context) {
    private var requestQueue: RequestQueue?
    private val ctx: Context

    init {
        ctx = context
        requestQueue = getRequestQueue()
    }

    private fun getRequestQueue(): RequestQueue? {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.applicationContext)
        }
        return requestQueue
    }

    fun addToRequestQueue(req: JsonObjectRequest) {
        getRequestQueue()!!.add(req)
    }

    companion object {
        private var instance: MySingleton? = null
        @Synchronized
        fun getInstance(context: Context): MySingleton? {
            if (instance == null) {
                instance = MySingleton(context)
            }
            return instance
        }
    }
}
