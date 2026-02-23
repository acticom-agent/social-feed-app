package com.example.socialfeed.data.repository

import com.example.socialfeed.data.db.dao.CommentDao
import com.example.socialfeed.data.db.dao.CommentWithUser
import com.example.socialfeed.data.db.entity.Comment
import kotlinx.coroutines.flow.Flow

class CommentRepository(private val commentDao: CommentDao) {
    suspend fun insert(comment: Comment) = commentDao.insert(comment)
    fun getCommentsForPost(postId: String): Flow<List<CommentWithUser>> =
        commentDao.getCommentsForPost(postId)
    suspend fun getAll() = commentDao.getAll()
    suspend fun deleteAll() = commentDao.deleteAll()
}
