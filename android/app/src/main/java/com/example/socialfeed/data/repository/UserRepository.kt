package com.example.socialfeed.data.repository

import com.example.socialfeed.data.db.dao.UserDao
import com.example.socialfeed.data.db.entity.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    suspend fun insert(user: User) = userDao.insert(user)
    suspend fun update(user: User) = userDao.update(user)
    suspend fun getById(id: String) = userDao.getById(id)
    fun observeById(id: String): Flow<User?> = userDao.observeById(id)
    suspend fun getAll() = userDao.getAll()
    suspend fun deleteAll() = userDao.deleteAll()
}
