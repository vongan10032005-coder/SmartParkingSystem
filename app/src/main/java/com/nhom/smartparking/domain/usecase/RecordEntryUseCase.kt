package com.nhom.smartparking.domain.usecase

import com.nhom.smartparking.domain.model.VehicleRecord
import com.nhom.smartparking.domain.repository.ParkingRepository
import javax.inject.Inject

class RecordEntryUseCase @Inject constructor(
    private val repository: ParkingRepository
) {
    suspend operator fun invoke(licensePlate: String): Result<VehicleRecord> {
        if (licensePlate.isBlank())
            return Result.failure(IllegalArgumentException("Biển số không được trống"))

        val cleanPlate = licensePlate.uppercase().trim()

        if (!isValidPlate(cleanPlate))
            return Result.failure(IllegalArgumentException("Biển số không hợp lệ: $cleanPlate"))

        val existing = repository.getActiveRecord(cleanPlate)
        if (existing != null)
            return Result.failure(IllegalStateException("Xe $cleanPlate đang trong bãi"))

        return repository.recordEntry(cleanPlate)
    }

    private fun isValidPlate(plate: String): Boolean {
        val regex = Regex("""^\d{2}[A-Z]\d?-\d{4,5}$""")
        return regex.matches(plate)
    }
}