package com.example.socialfeed.ui.screens.create

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.socialfeed.viewmodel.CreatePostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onPostCreated: () -> Unit,
    viewModel: CreatePostViewModel = viewModel()
) {
    val text by viewModel.text.collectAsState()
    val imageUri by viewModel.imageUri.collectAsState()
    val isPosting by viewModel.isPosting.collectAsState()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> viewModel.onImageSelected(uri) }

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

            // Image preview
            if (imageUri != null) {
                Box {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Selected image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { viewModel.clearImage() },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Remove image",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Add image button
            OutlinedButton(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Filled.Image, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Image")
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
