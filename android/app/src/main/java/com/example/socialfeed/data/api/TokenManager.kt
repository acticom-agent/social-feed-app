package com.example.socialfeed.data.api

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    var token: String?
        get() = prefs.getString("jwt_token", null)
        set(value) = prefs.edit().putString("jwt_token", value).apply()

    var userId: Int
        get() = prefs.getInt("user_id", -1)
        set(value) = prefs.edit().putInt("user_id", value).apply()

    var username: String?
        get() = prefs.getString("username", null)
        set(value) = prefs.edit().putString("username", value).apply()

    val isLoggedIn: Boolean get() = token != null

    fun clear() {
        prefs.edit().clear().apply()
    }
}
