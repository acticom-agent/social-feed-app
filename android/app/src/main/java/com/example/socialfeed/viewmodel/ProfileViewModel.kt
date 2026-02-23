package com.example.socialfeed.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialfeed.SocialFeedApp
import com.example.socialfeed.data.api.ApiClient
import com.example.socialfeed.data.api.ApiPost
import com.example.socialfeed.data.api.ApiUser
import com.example.socialfeed.data.repository.PostRepository
import com.example.socialfeed.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepo = UserRepository(ApiClient.service)
    private val postRepo = PostRepository(ApiClient.service)
    private val tokenManager = (application as SocialFeedApp).tokenManager

    private val _user = MutableStateFlow<ApiUser?>(null)
    val user: StateFlow<ApiUser?> = _user

    private val _posts = MutableStateFlow<List<ApiPost>>(emptyList())
    val posts: StateFlow<List<ApiPost>> = _posts

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            val userId = tokenManager.userId
            if (userId > 0) {
                userRepo.getUser(userId).onSuccess { _user.value = it }
                // Filter user's posts from feed
                postRepo.getFeed(100, 0).onSuccess { allPosts ->
                    _posts.value = allPosts.filter { it.author.id == userId }
                }
            }
        }
    }
}
