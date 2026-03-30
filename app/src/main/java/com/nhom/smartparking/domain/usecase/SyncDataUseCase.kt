package com.nhom.smartparking.domain.usecase

import com.nhom.smartparking.domain.repository.ParkingRepository
import javax.inject.Inject

class SyncDataUseCase @Inject constructor(
    private val repository: ParkingRepository
) {
    suspend operator fun invoke(): Result<Unit> = repository.syncOfflineData()
}