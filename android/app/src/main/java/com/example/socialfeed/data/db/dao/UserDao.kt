package com.example.socialfeed.data.db.dao

import androidx.room.*
import com.example.socialfeed.data.db.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    fun observeById(id: String): Flow<User?>

    @Query("SELECT * FROM users")
    suspend fun getAll(): List<User>

    @Query("DELETE FROM users")
    suspend fun deleteAll()
}
