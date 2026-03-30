package com.nhom.smartparking.domain.usecase

import com.nhom.smartparking.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<String> {
        if (email.isBlank() || password.isBlank())
            return Result.failure(IllegalArgumentException("Email và mật khẩu không được trống"))
        return repository.login(email, password)
    }
}