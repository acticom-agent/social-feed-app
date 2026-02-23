package com.example.socialfeed.data.api

import com.google.gson.annotations.SerializedName

// Auth
data class LoginRequest(val username: String, val password: String)
data class RegisterRequest(val username: String, val password: String)
data class AuthResponse(val token: String, val user: ApiUser)

// User
data class ApiUser(
    val id: Int,
    val username: String,
    val avatarUrl: String?,
    @SerializedName("_count") val count: UserCount? = null
)

data class UserCount(val posts: Int = 0)

data class UpdateUserRequest(val username: String? = null, val avatarUrl: String? = null)

// Post
data class PostsResponse(val posts: List<ApiPost>)

data class ApiPost(
    val id: Int,
    val text: String,
    val imageUrl: String?,
    val createdAt: String,
    val author: ApiUser,
    @SerializedName("_count") val count: PostCount? = null,
    val liked: Boolean? = null
)

data class PostCount(val likes: Int = 0, val comments: Int = 0)

data class CreatePostRequest(val text: String, val imageUrl: String? = null)

// Like
data class LikeResponse(val liked: Boolean, val likesCount: Int)

// Comment
data class CommentsResponse(val comments: List<ApiComment>)

data class ApiComment(
    val id: Int,
    val text: String,
    val createdAt: String,
    val author: ApiUser
)

data class CreateCommentRequest(val text: String)
