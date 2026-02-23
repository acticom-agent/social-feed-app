package com.example.socialfeed.data.db.dao

import androidx.room.*
import com.example.socialfeed.data.db.entity.Like
import kotlinx.coroutines.flow.Flow

@Dao
interface LikeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(like: Like)

    @Delete
    suspend fun delete(like: Like)

    @Query("SELECT EXISTS(SELECT 1 FROM likes WHERE postId = :postId AND userId = :userId)")
    fun isLiked(postId: String, userId: String): Flow<Boolean>

    @Query("SELECT COUNT(*) FROM likes WHERE postId = :postId")
    fun getLikeCount(postId: String): Flow<Int>

    @Query("DELETE FROM likes")
    suspend fun deleteAll()

    @Query("SELECT * FROM likes")
    suspend fun getAll(): List<Like>
}
