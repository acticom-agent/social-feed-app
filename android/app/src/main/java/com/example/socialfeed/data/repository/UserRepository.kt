package com.example.socialfeed.data.repository

import com.example.socialfeed.data.api.*

class UserRepository(private val api: ApiService) {
    suspend fun getUser(id: Int): Result<ApiUser> = runCatching {
        api.getUser(id)
    }

    suspend fun updateMe(username: String? = null, avatarUrl: String? = null): Result<ApiUser> = runCatching {
        api.updateMe(UpdateUserRequest(username, avatarUrl))
    }
}
