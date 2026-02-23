package com.example.socialfeed

import android.app.Application
import com.example.socialfeed.data.api.ApiClient
import com.example.socialfeed.data.api.TokenManager

class SocialFeedApp : Application() {
    lateinit var tokenManager: TokenManager

    override fun onCreate() {
        super.onCreate()
        tokenManager = TokenManager(this)
        ApiClient.init { tokenManager.token }
    }
}
