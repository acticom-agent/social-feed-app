package com.example.socialfeed.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialfeed.data.api.ApiClient
import com.example.socialfeed.data.repository.PostRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CreatePostViewModel(application: Application) : AndroidViewModel(application) {
    private val postRepo = PostRepository(ApiClient.service)

    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text

    private val _imageUrl = MutableStateFlow("")
    val imageUrl: StateFlow<String> = _imageUrl

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun onTextChange(value: String) { _text.value = value }
    fun onImageUrlChange(value: String) { _imageUrl.value = value }

    fun createPost(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isPosting.value = true
            _error.value = null
            val imgUrl = _imageUrl.value.trim().ifBlank { null }
            postRepo.createPost(_text.value.trim(), imgUrl)
                .onSuccess {
                    _text.value = ""
                    _imageUrl.value = ""
                    _isPosting.value = false
                    onSuccess()
                }
                .onFailure {
                    _error.value = it.message
                    _isPosting.value = false
                }
        }
    }
}
