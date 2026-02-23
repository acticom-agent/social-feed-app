package com.example.socialfeed.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialfeed.SocialFeedApp
import com.example.socialfeed.data.api.ApiClient
import com.example.socialfeed.data.api.ApiPost
import com.example.socialfeed.data.repository.PostRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FeedViewModel(application: Application) : AndroidViewModel(application) {
    private val postRepo = PostRepository(ApiClient.service)
    private val tokenManager = (application as SocialFeedApp).tokenManager

    private val _posts = MutableStateFlow<List<ApiPost>>(emptyList())
    val posts: StateFlow<List<ApiPost>> = _posts

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _likedPosts = MutableStateFlow<Set<Int>>(emptySet())
    val likedPosts: StateFlow<Set<Int>> = _likedPosts

    private var offset = 0
    private val pageSize = 20

    init {
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            _isRefreshing.value = true
            offset = 0
            postRepo.getFeed(pageSize, 0).onSuccess {
                _posts.value = it
                offset = it.size
            }
            _isRefreshing.value = false
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            postRepo.getFeed(pageSize, offset).onSuccess { newPosts ->
                if (newPosts.isNotEmpty()) {
                    _posts.value = _posts.value + newPosts
                    offset += newPosts.size
                }
            }
        }
    }

    fun toggleLike(postId: Int) {
        viewModelScope.launch {
            postRepo.toggleLike(postId).onSuccess { response ->
                if (response.liked) {
                    _likedPosts.value = _likedPosts.value + postId
                } else {
                    _likedPosts.value = _likedPosts.value - postId
                }
                // Update like count in posts list
                _posts.value = _posts.value.map { post ->
                    if (post.id == postId) {
                        post.copy(
                            count = post.count?.copy(likes = response.likesCount)
                                ?: com.example.socialfeed.data.api.PostCount(likes = response.likesCount)
                        )
                    } else post
                }
            }
        }
    }
}
