package com.nhom.smartparking.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle_records")
data class VehicleRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val licensePlate: String,
    val entryTime: Long,
    val exitTime: Long? = null,
    val fee: Double? = null,
    val isSynced: Boolean = false
)