package com.example.socialfeed.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences(private val context: Context) {
    companion object {
        val IS_PROFILE_SETUP = booleanPreferencesKey("is_profile_setup")
        val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val isProfileSetup: Flow<Boolean> = context.dataStore.data.map { it[IS_PROFILE_SETUP] ?: false }
    val currentUserId: Flow<String?> = context.dataStore.data.map { it[CURRENT_USER_ID] }
    val darkMode: Flow<Boolean?> = context.dataStore.data.map { it[DARK_MODE] }

    suspend fun setProfileSetup(userId: String) {
        context.dataStore.edit {
            it[IS_PROFILE_SETUP] = true
            it[CURRENT_USER_ID] = userId
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = enabled }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
