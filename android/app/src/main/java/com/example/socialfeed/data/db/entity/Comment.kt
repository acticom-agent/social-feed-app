package com.example.socialfeed.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comments",
    foreignKeys = [
        ForeignKey(entity = Post::class, parentColumns = ["id"], childColumns = ["postId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["authorId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("postId"), Index("authorId")]
)
data class Comment(
    @PrimaryKey val id: String,
    val postId: String,
    val authorId: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)
