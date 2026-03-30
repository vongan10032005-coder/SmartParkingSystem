package com.nhom.smartparking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom.smartparking.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) = viewModelScope.launch {
        _uiState.value = LoginUiState.Loading
        loginUseCase(email, password).fold(
            onSuccess = { _uiState.value = LoginUiState.Success },
            onFailure = { _uiState.value = LoginUiState.Error(it.message ?: "Lỗi đăng nhập") }
        )
    }

    fun resetState() { _uiState.value = LoginUiState.Idle }
}

sealed class LoginUiState {
    object Idle    : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}