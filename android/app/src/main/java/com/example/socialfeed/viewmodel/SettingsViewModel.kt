package com.example.socialfeed.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialfeed.SocialFeedApp
import com.example.socialfeed.data.datastore.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = UserPreferences(application)
    private val tokenManager = (application as SocialFeedApp).tokenManager

    val darkMode: StateFlow<Boolean?> = prefs.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { prefs.setDarkMode(enabled) }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clear()
            prefs.clear()
            onDone()
        }
    }
}
