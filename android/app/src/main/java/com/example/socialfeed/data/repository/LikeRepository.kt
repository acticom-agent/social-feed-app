package com.example.socialfeed.data.repository

import com.example.socialfeed.data.db.dao.LikeDao
import com.example.socialfeed.data.db.entity.Like
import kotlinx.coroutines.flow.Flow

class LikeRepository(private val likeDao: LikeDao) {
    suspend fun toggleLike(postId: String, userId: String) {
        val like = Like(postId, userId)
        // Try delete first; if nothing deleted, insert
        likeDao.delete(like)
        // We need a different approach - check then act
    }

    suspend fun like(postId: String, userId: String) = likeDao.insert(Like(postId, userId))
    suspend fun unlike(postId: String, userId: String) = likeDao.delete(Like(postId, userId))
    fun isLiked(postId: String, userId: String): Flow<Boolean> = likeDao.isLiked(postId, userId)
    fun getLikeCount(postId: String): Flow<Int> = likeDao.getLikeCount(postId)
    suspend fun getAll() = likeDao.getAll()
    suspend fun deleteAll() = likeDao.deleteAll()
}
