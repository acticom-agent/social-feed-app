package com.example.socialfeed.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "likes",
    primaryKeys = ["postId", "userId"],
    foreignKeys = [
        ForeignKey(entity = Post::class, parentColumns = ["id"], childColumns = ["postId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("postId"), Index("userId")]
)
data class Like(
    val postId: String,
    val userId: String
)
