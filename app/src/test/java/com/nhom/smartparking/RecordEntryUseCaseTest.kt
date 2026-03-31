package com.nhom.smartparking

import com.nhom.smartparking.domain.model.VehicleRecord
import com.nhom.smartparking.domain.repository.ParkingRepository
import com.nhom.smartparking.domain.usecase.RecordEntryUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class RecordEntryUseCaseTest {

    private lateinit var repository: ParkingRepository
    private lateinit var useCase: RecordEntryUseCase

    @Before
    fun setup() {
        repository = mock()
        useCase    = RecordEntryUseCase(repository)
    }

    @Test
    fun `empty plate returns failure`() = runTest {
        val result = useCase("")
        assertTrue(result.isFailure)
        assertEquals("Biển số không được trống", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invalid plate format returns failure`() = runTest {
        val result = useCase("ABCDEF")
        assertTrue(result.isFailure)
    }

    @Test
    fun `plate already parked returns failure`() = runTest {
        val plate  = "51A-12345"
        val record = VehicleRecord(1L, plate, System.currentTimeMillis())
        whenever(repository.getActiveRecord(plate)).thenReturn(record)
        val result = useCase(plate)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("đang trong bãi") == true)
    }

    @Test
    fun `valid plate not parked records entry successfully`() = runTest {
        val plate  = "51A-12345"
        val record = VehicleRecord(1L, plate, System.currentTimeMillis())
        whenever(repository.getActiveRecord(plate)).thenReturn(null)
        whenever(repository.recordEntry(plate)).thenReturn(Result.success(record))
        val result = useCase(plate)
        assertTrue(result.isSuccess)
        assertEquals(plate, result.getOrNull()?.licensePlate)
    }
}