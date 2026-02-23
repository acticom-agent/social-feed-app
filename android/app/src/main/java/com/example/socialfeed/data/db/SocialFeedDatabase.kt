package com.example.socialfeed.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.socialfeed.data.db.dao.*
import com.example.socialfeed.data.db.entity.*

@Database(
    entities = [User::class, Post::class, Like::class, Comment::class],
    version = 1,
    exportSchema = false
)
abstract class SocialFeedDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun likeDao(): LikeDao
    abstract fun commentDao(): CommentDao

    companion object {
        @Volatile
        private var INSTANCE: SocialFeedDatabase? = null

        fun getInstance(context: Context): SocialFeedDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SocialFeedDatabase::class.java,
                    "social_feed_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
