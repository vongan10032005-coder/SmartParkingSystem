package com.nhom.smartparking.data.local.dao

import androidx.room.*
import com.nhom.smartparking.data.local.entity.VehicleRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: VehicleRecordEntity): Long

    @Update
    suspend fun update(record: VehicleRecordEntity)

    @Query("SELECT * FROM vehicle_records ORDER BY entryTime DESC")
    fun getAllRecords(): Flow<List<VehicleRecordEntity>>

    @Query("SELECT * FROM vehicle_records WHERE licensePlate = :plate AND exitTime IS NULL LIMIT 1")
    suspend fun getActiveRecord(plate: String): VehicleRecordEntity?

    @Query("SELECT * FROM vehicle_records WHERE isSynced = 0")
    suspend fun getUnsyncedRecords(): List<VehicleRecordEntity>

    @Query("UPDATE vehicle_records SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)

    @Query("SELECT * FROM vehicle_records ORDER BY entryTime DESC LIMIT :limit OFFSET :offset")
    suspend fun getRecordsPaged(limit: Int, offset: Int): List<VehicleRecordEntity>
}