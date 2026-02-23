package com.example.socialfeed.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "posts",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["authorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("authorId")]
)
data class Post(
    @PrimaryKey val id: String,
    val authorId: String,
    val text: String,
    val imagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
