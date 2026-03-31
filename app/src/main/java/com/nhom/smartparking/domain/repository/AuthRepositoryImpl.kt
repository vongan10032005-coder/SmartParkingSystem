package com.nhom.smartparking.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nhom.smartparking.data.remote.api.ParkingApiService
import com.nhom.smartparking.data.remote.dto.LoginRequest
import com.nhom.smartparking.data.remote.interceptor.AuthInterceptor
import com.nhom.smartparking.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: ParkingApiService,
    private val dataStore: DataStore<Preferences>,
    private val authInterceptor: AuthInterceptor
) : AuthRepository {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val token = response.body()!!.token
                saveToken(token)
                authInterceptor.setToken(token)
                Result.success(token)
            } else {
                Result.failure(Exception("Đăng nhập thất bại: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    override suspend fun logout() {
        try { api.logout() } catch (_: Exception) {}
        dataStore.edit { it.remove(TOKEN_KEY) }
        authInterceptor.setToken("")
    }

    override suspend fun getToken(): String? =
        dataStore.data.map { it[TOKEN_KEY] }.first()

    override fun isLoggedIn(): Boolean = false // sẽ override bằng Flow ở ViewModel

    private suspend fun saveToken(token: String) {
        dataStore.edit { it[TOKEN_KEY] = token }
    }
}