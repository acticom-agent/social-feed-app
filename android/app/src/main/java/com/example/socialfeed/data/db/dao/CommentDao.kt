package com.example.socialfeed.data.db.dao

import androidx.room.*
import com.example.socialfeed.data.db.entity.Comment
import kotlinx.coroutines.flow.Flow

data class CommentWithUser(
    val id: String,
    val postId: String,
    val authorId: String,
    val text: String,
    val createdAt: Long,
    val username: String,
    val avatarPath: String?
)

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: Comment)

    @Query("""
        SELECT c.id, c.postId, c.authorId, c.text, c.createdAt,
               u.username, u.avatarPath
        FROM comments c
        INNER JOIN users u ON c.authorId = u.id
        WHERE c.postId = :postId
        ORDER BY c.createdAt ASC
    """)
    fun getCommentsForPost(postId: String): Flow<List<CommentWithUser>>

    @Query("DELETE FROM comments")
    suspend fun deleteAll()

    @Query("SELECT * FROM comments")
    suspend fun getAll(): List<Comment>
}
