package com.nhom.smartparking.domain.usecase

import com.nhom.smartparking.domain.model.VehicleRecord
import com.nhom.smartparking.domain.repository.ParkingRepository
import javax.inject.Inject

class RecordExitUseCase @Inject constructor(
    private val repository: ParkingRepository
) {
    suspend operator fun invoke(licensePlate: String): Result<VehicleRecord> {
        val cleanPlate = licensePlate.uppercase().trim()

        val active = repository.getActiveRecord(cleanPlate)
            ?: return Result.failure(
                IllegalStateException("Xe $cleanPlate không có trong bãi")
            )

        return repository.recordExit(cleanPlate)
    }
}