package com.example.socialfeed.data.repository

import com.example.socialfeed.data.api.*

class PostRepository(private val api: ApiService) {
    suspend fun getFeed(limit: Int = 20, offset: Int = 0): Result<List<ApiPost>> = runCatching {
        api.getPosts(limit, offset).posts
    }

    suspend fun createPost(text: String, imageUrl: String? = null): Result<ApiPost> = runCatching {
        api.createPost(CreatePostRequest(text, imageUrl))
    }

    suspend fun deletePost(id: Int): Result<Unit> = runCatching {
        api.deletePost(id)
    }

    suspend fun toggleLike(postId: Int): Result<LikeResponse> = runCatching {
        api.toggleLike(postId)
    }

    suspend fun getComments(postId: Int): Result<List<ApiComment>> = runCatching {
        api.getComments(postId).comments
    }

    suspend fun addComment(postId: Int, text: String): Result<ApiComment> = runCatching {
        api.createComment(postId, CreateCommentRequest(text))
    }
}
