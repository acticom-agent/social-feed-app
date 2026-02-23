package com.example.socialfeed.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.socialfeed.data.db.SocialFeedDatabase
import com.example.socialfeed.data.db.dao.CommentWithUser
import com.example.socialfeed.data.db.dao.PostWithDetails
import com.example.socialfeed.data.db.entity.Comment
import com.example.socialfeed.data.datastore.UserPreferences
import com.example.socialfeed.data.repository.CommentRepository
import com.example.socialfeed.data.repository.LikeRepository
import com.example.socialfeed.data.repository.PostRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class PostDetailViewModel(application: Application, private val postId: String) : AndroidViewModel(application) {
    private val db = SocialFeedDatabase.getInstance(application)
    private val postRepo = PostRepository(db.postDao())
    private val commentRepo = CommentRepository(db.commentDao())
    private val likeRepo = LikeRepository(db.likeDao())
    private val prefs = UserPreferences(application)

    val currentUserId: StateFlow<String?> = prefs.currentUserId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val post: StateFlow<PostWithDetails?> = postRepo.getPostWithDetails(postId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val comments: StateFlow<List<CommentWithUser>> = commentRepo.getCommentsForPost(postId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val isLiked: StateFlow<Boolean> = currentUserId.flatMapLatest { userId ->
        if (userId != null) likeRepo.isLiked(postId, userId) else flowOf(false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _commentText = MutableStateFlow("")
    val commentText: StateFlow<String> = _commentText

    fun onCommentTextChange(value: String) { _commentText.value = value }

    fun toggleLike() {
        viewModelScope.launch {
            val userId = currentUserId.value ?: return@launch
            if (isLiked.value) likeRepo.unlike(postId, userId)
            else likeRepo.like(postId, userId)
        }
    }

    fun addComment() {
        viewModelScope.launch {
            val userId = currentUserId.value ?: return@launch
            val text = _commentText.value.trim()
            if (text.isBlank()) return@launch
            commentRepo.insert(
                Comment(
                    id = UUID.randomUUID().toString(),
                    postId = postId,
                    authorId = userId,
                    text = text
                )
            )
            _commentText.value = ""
        }
    }

    class Factory(private val application: Application, private val postId: String) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PostDetailViewModel(application, postId) as T
        }
    }
}
