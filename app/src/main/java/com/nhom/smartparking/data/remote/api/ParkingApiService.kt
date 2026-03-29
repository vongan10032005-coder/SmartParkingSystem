package com.nhom.smartparking.data.remote.api

import com.nhom.smartparking.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ParkingApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/logout")
    suspend fun logout(): Response<Unit>

    @POST("api/vehicles/entry")
    suspend fun recordEntry(@Body request: VehicleEntryRequest): Response<VehicleRecordResponse>

    @PUT("api/vehicles/{id}/exit")
    suspend fun recordExit(
        @Path("id") recordId: Long,
        @Body request: VehicleExitRequest
    ): Response<VehicleRecordResponse>

    @GET("api/vehicles/history")
    suspend fun getHistory(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): Response<HistoryResponse>

    @POST("api/vehicles/sync")
    suspend fun syncOfflineData(@Body records: List<SyncRequest>): Response<SyncResponse>
}