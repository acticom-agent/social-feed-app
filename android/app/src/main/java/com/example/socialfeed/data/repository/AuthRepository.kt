package com.example.socialfeed.data.repository

import com.example.socialfeed.data.api.*

class AuthRepository(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(username: String, password: String): Result<ApiUser> = runCatching {
        val response = api.login(LoginRequest(username, password))
        tokenManager.token = response.token
        tokenManager.userId = response.user.id
        tokenManager.username = response.user.username
        response.user
    }

    suspend fun register(username: String, password: String): Result<ApiUser> = runCatching {
        val response = api.register(RegisterRequest(username, password))
        tokenManager.token = response.token
        tokenManager.userId = response.user.id
        tokenManager.username = response.user.username
        response.user
    }

    suspend fun getMe(): Result<ApiUser> = runCatching { api.getMe() }

    fun isLoggedIn() = tokenManager.isLoggedIn

    fun logout() = tokenManager.clear()

    fun getCurrentUserId() = tokenManager.userId
    fun getUsername() = tokenManager.username
}
