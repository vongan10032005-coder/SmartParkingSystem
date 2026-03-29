package com.nhom.smartparking.data.repository

import com.nhom.smartparking.data.local.dao.VehicleRecordDao
import com.nhom.smartparking.data.local.entity.VehicleRecordEntity
import com.nhom.smartparking.data.remote.api.ParkingApiService
import com.nhom.smartparking.data.remote.dto.SyncRequest
import com.nhom.smartparking.data.remote.dto.VehicleEntryRequest
import com.nhom.smartparking.data.remote.dto.VehicleExitRequest
import com.nhom.smartparking.domain.model.VehicleRecord
import com.nhom.smartparking.domain.repository.ParkingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ParkingRepositoryImpl @Inject constructor(
    private val dao: VehicleRecordDao,
    private val api: ParkingApiService
) : ParkingRepository {

    override fun getAllRecords(): Flow<List<VehicleRecord>> =
        dao.getAllRecords().map { list -> list.map { it.toDomain() } }

    override suspend fun getActiveRecord(plate: String): VehicleRecord? =
        dao.getActiveRecord(plate)?.toDomain()

    override suspend fun recordEntry(licensePlate: String): Result<VehicleRecord> {
        val now = System.currentTimeMillis()
        val entity = VehicleRecordEntity(
            licensePlate = licensePlate,
            entryTime = now,
            isSynced = false
        )
        val id = dao.insert(entity)

        try {
            val response = api.recordEntry(VehicleEntryRequest(licensePlate, now))
            if (response.isSuccessful) dao.markAsSynced(id)
        } catch (_: Exception) {
            // Offline — WorkManager sẽ sync sau
        }

        return Result.success(entity.copy(id = id).toDomain())
    }

    override suspend fun recordExit(licensePlate: String): Result<VehicleRecord> {
        val active = dao.getActiveRecord(licensePlate)
            ?: return Result.failure(Exception("Không tìm thấy xe trong bãi"))

        val now = System.currentTimeMillis()
        val hours = Math.ceil((now - active.entryTime) / 3_600_000.0)
        val fee = hours * 5000.0

        val updated = active.copy(exitTime = now, fee = fee, isSynced = false)
        dao.update(updated)

        try {
            val response = api.recordExit(active.id, VehicleExitRequest(now))
            if (response.isSuccessful) dao.markAsSynced(active.id)
        } catch (_: Exception) {
            // Offline
        }

        return Result.success(updated.toDomain())
    }

    override suspend fun syncOfflineData(): Result<Unit> {
        val unsynced = dao.getUnsyncedRecords()
        if (unsynced.isEmpty()) return Result.success(Unit)

        val requests = unsynced.map {
            SyncRequest(it.licensePlate, it.entryTime, it.exitTime, it.fee)
        }

        return try {
            val response = api.syncOfflineData(requests)
            if (response.isSuccessful) {
                unsynced.forEach { dao.markAsSynced(it.id) }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Sync thất bại: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun VehicleRecordEntity.toDomain() = VehicleRecord(
        id = id,
        licensePlate = licensePlate,
        entryTime = entryTime,
        exitTime = exitTime,
        fee = fee,
        isSynced = isSynced
    )
}