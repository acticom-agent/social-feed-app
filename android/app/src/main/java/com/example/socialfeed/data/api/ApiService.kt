package com.example.socialfeed.data.api

import retrofit2.http.*

interface ApiService {
    // Auth
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("api/auth/me")
    suspend fun getMe(): ApiUser

    // Posts
    @GET("api/posts")
    suspend fun getPosts(@Query("limit") limit: Int = 20, @Query("offset") offset: Int = 0): PostsResponse

    @POST("api/posts")
    suspend fun createPost(@Body request: CreatePostRequest): ApiPost

    @DELETE("api/posts/{id}")
    suspend fun deletePost(@Path("id") id: Int)

    // Likes
    @POST("api/posts/{id}/like")
    suspend fun toggleLike(@Path("id") id: Int): LikeResponse

    // Comments
    @GET("api/posts/{id}/comments")
    suspend fun getComments(@Path("id") id: Int): CommentsResponse

    @POST("api/posts/{id}/comments")
    suspend fun createComment(@Path("id") id: Int, @Body request: CreateCommentRequest): ApiComment

    // Users
    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id: Int): ApiUser

    @PUT("api/users/me")
    suspend fun updateMe(@Body request: UpdateUserRequest): ApiUser
}
