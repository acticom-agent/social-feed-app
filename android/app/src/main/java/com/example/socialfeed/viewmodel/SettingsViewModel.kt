package com.example.socialfeed.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialfeed.data.db.SocialFeedDatabase
import com.example.socialfeed.data.datastore.UserPreferences
import com.example.socialfeed.data.repository.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SocialFeedDatabase.getInstance(application)
    private val userRepo = UserRepository(db.userDao())
    private val postRepo = PostRepository(db.postDao())
    private val likeRepo = LikeRepository(db.likeDao())
    private val commentRepo = CommentRepository(db.commentDao())
    private val prefs = UserPreferences(application)

    val darkMode: StateFlow<Boolean?> = prefs.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { prefs.setDarkMode(enabled) }
    }

    fun clearAllData(onDone: () -> Unit) {
        viewModelScope.launch {
            commentRepo.deleteAll()
            likeRepo.deleteAll()
            postRepo.deleteAll()
            userRepo.deleteAll()
            prefs.clear()
            // Delete image files
            val context = getApplication<Application>()
            context.filesDir.listFiles()?.forEach { it.delete() }
            onDone()
        }
    }

    fun exportAsJson(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val data = mapOf(
                "users" to userRepo.getAll(),
                "posts" to postRepo.getAll(),
                "likes" to likeRepo.getAll(),
                "comments" to commentRepo.getAll()
            )
            val json = GsonBuilder().setPrettyPrinting().create().toJson(data)
            val context = getApplication<Application>()
            val file = File(context.cacheDir, "social_feed_export.json")
            file.writeText(json)
            onResult(file.absolutePath)
        }
    }

    fun resetProfile(onDone: () -> Unit) {
        viewModelScope.launch {
            prefs.clear()
            onDone()
        }
    }
}
