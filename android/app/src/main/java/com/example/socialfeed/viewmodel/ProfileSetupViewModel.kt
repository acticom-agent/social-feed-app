package com.example.socialfeed.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialfeed.data.db.SocialFeedDatabase
import com.example.socialfeed.data.db.entity.User
import com.example.socialfeed.data.datastore.UserPreferences
import com.example.socialfeed.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class ProfileSetupViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SocialFeedDatabase.getInstance(application)
    private val userRepo = UserRepository(db.userDao())
    private val prefs = UserPreferences(application)

    val isProfileSetup: StateFlow<Boolean> = prefs.isProfileSetup
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _avatarUri = MutableStateFlow<Uri?>(null)
    val avatarUri: StateFlow<Uri?> = _avatarUri

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun onUsernameChange(value: String) { _username.value = value }
    fun onAvatarSelected(uri: Uri?) { _avatarUri.value = uri }

    fun createProfile(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = UUID.randomUUID().toString()
            var avatarPath: String? = null

            _avatarUri.value?.let { uri ->
                val context = getApplication<Application>()
                val file = File(context.filesDir, "avatar_$userId.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output -> input.copyTo(output) }
                }
                avatarPath = file.absolutePath
            }

            val user = User(id = userId, username = _username.value.trim(), avatarPath = avatarPath)
            userRepo.insert(user)
            prefs.setProfileSetup(userId)
            _isLoading.value = false
            onSuccess()
        }
    }
}
