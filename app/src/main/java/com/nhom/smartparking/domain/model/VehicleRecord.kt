package com.nhom.smartparking.domain.model

data class VehicleRecord(
    val id: Long = 0,
    val licensePlate: String,
    val entryTime: Long,
    val exitTime: Long? = null,
    val fee: Double? = null,
    val isSynced: Boolean = false
) {
    fun isParked(): Boolean = exitTime == null

    fun durationMinutes(): Long {
        val end = exitTime ?: System.currentTimeMillis()
        return (end - entryTime) / 60_000
    }

    fun formattedFee(): String {
        return if (fee != null) "${fee.toLong()}đ" else "Chưa tính"
    }
}