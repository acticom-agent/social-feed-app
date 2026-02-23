package com.example.socialfeed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.socialfeed.data.datastore.UserPreferences
import com.example.socialfeed.ui.navigation.Screen
import com.example.socialfeed.ui.navigation.SocialFeedNavGraph
import com.example.socialfeed.ui.theme.SocialFeedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val prefs = remember { UserPreferences(context) }
            val isSetup by prefs.isProfileSetup.collectAsState(initial = null)
            val darkModePref by prefs.darkMode.collectAsState(initial = null)
            val systemDark = isSystemInDarkTheme()
            val darkMode = darkModePref ?: systemDark

            SocialFeedTheme(darkTheme = darkMode, dynamicColor = false) {
                when (isSetup) {
                    null -> {} // Loading
                    true -> SocialFeedNavGraph(startDestination = Screen.Feed.route)
                    false -> SocialFeedNavGraph(startDestination = Screen.Setup.route)
                }
            }
        }
    }
}
