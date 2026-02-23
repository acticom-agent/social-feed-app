package com.example.socialfeed.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.socialfeed.ui.components.PostCard
import com.example.socialfeed.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onPostClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val user by viewModel.user.collectAsState()
    val posts by viewModel.posts.collectAsState()

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Profile", fontWeight = FontWeight.Bold) },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Filled.Settings, "Settings")
                }
            }
        )

        LazyColumn {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    user?.let { u ->
                        if (u.avatarUrl != null) {
                            AsyncImage(
                                model = u.avatarUrl,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Surface(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        u.username.take(1).uppercase(),
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))
                        Text(
                            u.username,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${u.count?.posts ?: posts.size} posts",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Divider()
            }

            items(posts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    isLiked = false,
                    onLikeClick = {},
                    onClick = { onPostClick(post.id.toString()) }
                )
            }
        }
    }
}
