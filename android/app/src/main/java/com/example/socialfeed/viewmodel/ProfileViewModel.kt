package com.example.socialfeed.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialfeed.data.db.SocialFeedDatabase
import com.example.socialfeed.data.db.dao.PostWithDetails
import com.example.socialfeed.data.db.entity.User
import com.example.socialfeed.data.datastore.UserPreferences
import com.example.socialfeed.data.repository.PostRepository
import com.example.socialfeed.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SocialFeedDatabase.getInstance(application)
    private val userRepo = UserRepository(db.userDao())
    private val postRepo = PostRepository(db.postDao())
    private val prefs = UserPreferences(application)

    val currentUserId: StateFlow<String?> = prefs.currentUserId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val user: StateFlow<User?> = currentUserId.flatMapLatest { userId ->
        if (userId != null) userRepo.observeById(userId) else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val posts: StateFlow<List<PostWithDetails>> = currentUserId.flatMapLatest { userId ->
        if (userId != null) postRepo.getPostsByUser(userId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
