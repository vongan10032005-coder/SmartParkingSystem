package com.nhom.smartparking.domain.repository

import com.nhom.smartparking.domain.model.VehicleRecord
import kotlinx.coroutines.flow.Flow

interface ParkingRepository {
    fun getAllRecords(): Flow<List<VehicleRecord>>
    suspend fun getActiveRecord(plate: String): VehicleRecord?
    suspend fun recordEntry(licensePlate: String): Result<VehicleRecord>
    suspend fun recordExit(licensePlate: String): Result<VehicleRecord>
    suspend fun syncOfflineData(): Result<Unit>
}

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<String>
    suspend fun logout()
    suspend fun getToken(): String?
    fun isLoggedIn(): Boolean
}