package com.nhom.smartparking.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nhom.smartparking.data.local.dao.VehicleRecordDao
import com.nhom.smartparking.data.local.entity.VehicleRecordEntity

@Database(
    entities = [VehicleRecordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleRecordDao(): VehicleRecordDao
}