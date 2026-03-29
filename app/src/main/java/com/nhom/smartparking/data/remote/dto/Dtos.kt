package com.nhom.smartparking.data.remote.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: Int,
    val name: String,
    val email: String
)

data class VehicleEntryRequest(
    val license_plate: String,
    val entry_time: Long
)

data class VehicleExitRequest(
    val exit_time: Long
)

data class VehicleRecordResponse(
    val id: Long,
    val license_plate: String,
    val entry_time: Long,
    val exit_time: Long?,
    val fee: Double?
)

data class HistoryResponse(
    val data: List<VehicleRecordResponse>,
    val total: Int,
    val current_page: Int,
    val last_page: Int
)

data class SyncRequest(
    val license_plate: String,
    val entry_time: Long,
    val exit_time: Long?,
    val fee: Double?
)

data class SyncResponse(
    val synced: Int,
    val failed: Int
)