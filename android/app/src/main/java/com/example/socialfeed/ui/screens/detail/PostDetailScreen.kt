package com.example.socialfeed.ui.screens.detail

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.socialfeed.data.db.dao.CommentWithUser
import com.example.socialfeed.viewmodel.PostDetailViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String,
    onBack: () -> Unit,
    viewModel: PostDetailViewModel = viewModel(
        factory = PostDetailViewModel.Factory(
            LocalContext.current.applicationContext as Application,
            postId
        )
    )
) {
    val post by viewModel.post.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val isLiked by viewModel.isLiked.collectAsState()
    val commentText by viewModel.commentText.collectAsState()

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Post", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            }
        )

        post?.let { p ->
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                // Post content
                item {
                    Column {
                        // Header
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (p.avatarPath != null) {
                                AsyncImage(
                                    model = File(p.avatarPath),
                                    contentDescription = "Avatar",
                                    modifier = Modifier.size(40.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(p.username.take(1).uppercase())
                                    }
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(p.username, fontWeight = FontWeight.SemiBold)
                                Text(
                                    formatTime(p.createdAt),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (p.imagePath != null) {
                            AsyncImage(
                                model = File(p.imagePath),
                                contentDescription = "Post image",
                                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        if (p.text.isNotBlank()) {
                            Text(
                                p.text,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Like row
                        Row(
                            modifier = Modifier.padding(horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.toggleLike() }) {
                                Icon(
                                    if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like",
                                    tint = if (isLiked) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                "${p.likeCount} likes",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        HorizontalDivider()

                        Text(
                            "Comments (${comments.size})",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Comments
                items(comments, key = { it.id }) { comment ->
                    CommentItem(comment)
                }
            }

            // Comment input
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = viewModel::onCommentTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Add a comment...") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = { viewModel.addComment() },
                    enabled = commentText.isNotBlank()
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (commentText.isNotBlank()) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentItem(comment: CommentWithUser) {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (comment.avatarPath != null) {
            AsyncImage(
                model = File(comment.avatarPath),
                contentDescription = "Avatar",
                modifier = Modifier.size(28.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Surface(
                modifier = Modifier.size(28.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        comment.username.take(1).uppercase(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    comment.username,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    formatTime(comment.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(comment.text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun formatTime(millis: Long): String {
    val diff = System.currentTimeMillis() - millis
    return when {
        diff < 60_000 -> "just now"
        diff < 3_600_000 -> "${diff / 60_000}m"
        diff < 86_400_000 -> "${diff / 3_600_000}h"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(millis))
    }
}
