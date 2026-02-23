package com.example.socialfeed.ui.screens.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.socialfeed.viewmodel.CreatePostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onPostCreated: () -> Unit,
    viewModel: CreatePostViewModel = viewModel()
) {
    val text by viewModel.text.collectAsState()
    val imageUrl by viewModel.imageUrl.collectAsState()
    val isPosting by viewModel.isPosting.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("New Post", fontWeight = FontWeight.Bold) }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = viewModel::onTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                placeholder = { Text("What's on your mind?") },
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = imageUrl,
                onValueChange = viewModel::onImageUrlChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Image URL (optional)") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            if (error != null) {
                Text(
                    error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { viewModel.createPost(onPostCreated) },
                enabled = text.isNotBlank() && !isPosting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isPosting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Post", fontSize = 16.sp)
                }
            }
        }
    }
}
