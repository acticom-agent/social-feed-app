package com.example.socialfeed.data.repository

import com.example.socialfeed.data.db.dao.PostDao
import com.example.socialfeed.data.db.dao.PostWithDetails
import com.example.socialfeed.data.db.entity.Post
import kotlinx.coroutines.flow.Flow

class PostRepository(private val postDao: PostDao) {
    suspend fun insert(post: Post) = postDao.insert(post)
    fun getFeedPaginated(limit: Int, offset: Int): Flow<List<PostWithDetails>> =
        postDao.getFeedPaginated(limit, offset)
    fun getPostWithDetails(postId: String): Flow<PostWithDetails?> =
        postDao.getPostWithDetails(postId)
    fun getPostsByUser(userId: String): Flow<List<PostWithDetails>> =
        postDao.getPostsByUser(userId)
    suspend fun getAll() = postDao.getAll()
    suspend fun deleteAll() = postDao.deleteAll()
}
