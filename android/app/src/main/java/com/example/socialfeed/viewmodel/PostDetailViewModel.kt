package com.example.socialfeed.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.socialfeed.data.api.ApiClient
import com.example.socialfeed.data.api.ApiComment
import com.example.socialfeed.data.api.ApiPost
import com.example.socialfeed.data.repository.PostRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PostDetailViewModel(application: Application, private val postId: Int) : AndroidViewModel(application) {
    private val postRepo = PostRepository(ApiClient.service)

    private val _post = MutableStateFlow<ApiPost?>(null)
    val post: StateFlow<ApiPost?> = _post

    private val _comments = MutableStateFlow<List<ApiComment>>(emptyList())
    val comments: StateFlow<List<ApiComment>> = _comments

    private val _isLiked = MutableStateFlow(false)
    val isLiked: StateFlow<Boolean> = _isLiked

    private val _commentText = MutableStateFlow("")
    val commentText: StateFlow<String> = _commentText

    init {
        loadPost()
        loadComments()
    }

    private fun loadPost() {
        viewModelScope.launch {
            // We load the post from the feed endpoint with limit 100 and find it
            // Or we can just rely on comments endpoint. For now, post comes from feed.
            // Actually the API doesn't have a single-post endpoint, so we fetch from feed
            postRepo.getFeed(100, 0).onSuccess { posts ->
                _post.value = posts.find { it.id == postId }
            }
        }
    }

    private fun loadComments() {
        viewModelScope.launch {
            postRepo.getComments(postId).onSuccess {
                _comments.value = it
            }
        }
    }

    fun onCommentTextChange(value: String) { _commentText.value = value }

    fun toggleLike() {
        viewModelScope.launch {
            postRepo.toggleLike(postId).onSuccess { response ->
                _isLiked.value = response.liked
                _post.value = _post.value?.let { p ->
                    p.copy(count = p.count?.copy(likes = response.likesCount)
                        ?: com.example.socialfeed.data.api.PostCount(likes = response.likesCount))
                }
            }
        }
    }

    fun addComment() {
        viewModelScope.launch {
            val text = _commentText.value.trim()
            if (text.isBlank()) return@launch
            postRepo.addComment(postId, text).onSuccess {
                _commentText.value = ""
                loadComments()
            }
        }
    }

    class Factory(private val application: Application, private val postId: Int) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PostDetailViewModel(application, postId) as T
        }
    }
}
