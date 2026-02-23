package com.example.socialfeed.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialfeed.data.db.SocialFeedDatabase
import com.example.socialfeed.data.db.entity.Post
import com.example.socialfeed.data.datastore.UserPreferences
import com.example.socialfeed.data.repository.PostRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class CreatePostViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SocialFeedDatabase.getInstance(application)
    private val postRepo = PostRepository(db.postDao())
    private val prefs = UserPreferences(application)

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting

    fun onTextChange(value: String) { _text.value = value }
    fun onImageSelected(uri: Uri?) { _imageUri.value = uri }
    fun clearImage() { _imageUri.value = null }

    fun createPost(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isPosting.value = true
            val userId = prefs.currentUserId.first() ?: return@launch
            val postId = UUID.randomUUID().toString()
            var imagePath: String? = null

            _imageUri.value?.let { uri ->
                val context = getApplication<Application>()
                val file = File(context.filesDir, "post_$postId.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                }
                imagePath = file.absolutePath
            }

            val post = Post(
                id = postId,
                authorId = userId,
                text = _text.value.trim(),
                imagePath = imagePath
            )
            postRepo.insert(post)
            _text.value = ""
            _imageUri.value = null
            _isPosting.value = false
            onSuccess()
        }
    }
}
