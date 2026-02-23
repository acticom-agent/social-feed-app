package com.example.socialfeed.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialfeed.data.db.SocialFeedDatabase
import com.example.socialfeed.data.db.dao.PostWithDetails
import com.example.socialfeed.data.datastore.UserPreferences
import com.example.socialfeed.data.repository.LikeRepository
import com.example.socialfeed.data.repository.PostRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FeedViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SocialFeedDatabase.getInstance(application)
    private val postRepo = PostRepository(db.postDao())
    private val likeRepo = LikeRepository(db.likeDao())
    private val prefs = UserPreferences(application)

    private val _page = MutableStateFlow(0)
    private val pageSize = 20

    val currentUserId: StateFlow<String?> = prefs.currentUserId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val posts: StateFlow<List<PostWithDetails>> = _page.flatMapLatest { page ->
        postRepo.getFeedPaginated(pageSize * (page + 1), 0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun loadMore() {
        _page.value++
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            val userId = currentUserId.value ?: return@launch
            val liked = likeRepo.isLiked(postId, userId).first()
            if (liked) likeRepo.unlike(postId, userId)
            else likeRepo.like(postId, userId)
        }
    }

    fun isPostLiked(postId: String): Flow<Boolean> {
        return currentUserId.flatMapLatest { userId ->
            if (userId != null) likeRepo.isLiked(postId, userId)
            else flowOf(false)
        }
    }
}
