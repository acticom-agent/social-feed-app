package com.example.socialfeed.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialfeed.SocialFeedApp
import com.example.socialfeed.data.api.ApiClient
import com.example.socialfeed.data.repository.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val tokenManager = (application as SocialFeedApp).tokenManager
    private val authRepo = AuthRepository(ApiClient.service, tokenManager)

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isRegisterMode = MutableStateFlow(false)
    val isRegisterMode: StateFlow<Boolean> = _isRegisterMode

    fun onUsernameChange(value: String) { _username.value = value }
    fun onPasswordChange(value: String) { _password.value = value }
    fun toggleMode() { _isRegisterMode.value = !_isRegisterMode.value; _error.value = null }

    fun isLoggedIn() = authRepo.isLoggedIn()

    fun submit(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = if (_isRegisterMode.value) {
                authRepo.register(_username.value.trim(), _password.value)
            } else {
                authRepo.login(_username.value.trim(), _password.value)
            }
            _isLoading.value = false
            result.onSuccess { onSuccess() }
                .onFailure { _error.value = it.message ?: "Authentication failed" }
        }
    }
}
