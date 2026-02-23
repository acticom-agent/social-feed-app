package com.example.socialfeed.data.db.dao

import androidx.room.*
import com.example.socialfeed.data.db.entity.Post
import kotlinx.coroutines.flow.Flow

data class PostWithDetails(
    val id: String,
    val authorId: String,
    val text: String,
    val imagePath: String?,
    val createdAt: Long,
    val username: String,
    val avatarPath: String?,
    val likeCount: Int,
    val commentCount: Int
)

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: Post)

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getById(id: String): Post?

    @Query("""
        SELECT p.id, p.authorId, p.text, p.imagePath, p.createdAt,
               u.username, u.avatarPath,
               (SELECT COUNT(*) FROM likes WHERE postId = p.id) as likeCount,
               (SELECT COUNT(*) FROM comments WHERE postId = p.id) as commentCount
        FROM posts p
        INNER JOIN users u ON p.authorId = u.id
        ORDER BY p.createdAt DESC
        LIMIT :limit OFFSET :offset
    """)
    fun getFeedPaginated(limit: Int, offset: Int): Flow<List<PostWithDetails>>

    @Query("""
        SELECT p.id, p.authorId, p.text, p.imagePath, p.createdAt,
               u.username, u.avatarPath,
               (SELECT COUNT(*) FROM likes WHERE postId = p.id) as likeCount,
               (SELECT COUNT(*) FROM comments WHERE postId = p.id) as commentCount
        FROM posts p
        INNER JOIN users u ON p.authorId = u.id
        WHERE p.id = :postId
    """)
    fun getPostWithDetails(postId: String): Flow<PostWithDetails?>

    @Query("""
        SELECT p.id, p.authorId, p.text, p.imagePath, p.createdAt,
               u.username, u.avatarPath,
               (SELECT COUNT(*) FROM likes WHERE postId = p.id) as likeCount,
               (SELECT COUNT(*) FROM comments WHERE postId = p.id) as commentCount
        FROM posts p
        INNER JOIN users u ON p.authorId = u.id
        WHERE p.authorId = :userId
        ORDER BY p.createdAt DESC
    """)
    fun getPostsByUser(userId: String): Flow<List<PostWithDetails>>

    @Query("DELETE FROM posts")
    suspend fun deleteAll()

    @Query("SELECT * FROM posts")
    suspend fun getAll(): List<Post>
}
