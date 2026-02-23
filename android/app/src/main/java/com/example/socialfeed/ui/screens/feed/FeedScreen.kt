package com.example.socialfeed.ui.screens.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.socialfeed.ui.components.PostCard
import com.example.socialfeed.viewmodel.FeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onPostClick: (String) -> Unit,
    viewModel: FeedViewModel = viewModel()
) {
    val posts by viewModel.posts.collectAsState()
    val likedPosts by viewModel.likedPosts.collectAsState()
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= posts.size - 3
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && posts.isNotEmpty()) viewModel.loadMore()
    }

    Column(Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("Social Feed", fontWeight = FontWeight.Bold) }
        )

        if (posts.isEmpty()) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "No posts yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Create your first post!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(state = listState) {
                items(posts, key = { it.id }) { post ->
                    PostCard(
                        post = post,
                        isLiked = likedPosts.contains(post.id),
                        onLikeClick = { viewModel.toggleLike(post.id) },
                        onClick = { onPostClick(post.id.toString()) }
                    )
                }
            }
        }
    }
}
