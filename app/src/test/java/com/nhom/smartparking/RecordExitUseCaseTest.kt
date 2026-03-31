package com.nhom.smartparking

import com.nhom.smartparking.domain.model.VehicleRecord
import com.nhom.smartparking.domain.repository.ParkingRepository
import com.nhom.smartparking.domain.usecase.RecordExitUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class RecordExitUseCaseTest {

    private lateinit var repository: ParkingRepository
    private lateinit var useCase: RecordExitUseCase

    @Before
    fun setup() {
        repository = mock()
        useCase = RecordExitUseCase(repository)
    }

    @Test
    fun `plate not in parking returns failure`() = runTest {
        val plate = "51A-12345"
        whenever(repository.getActiveRecord(plate)).thenReturn(null)
        val result = useCase(plate)
        assertTrue(result.isFailure)
        assertTrue(
            result.exceptionOrNull()?.message?.contains("không có trong bãi") == true
        )
    }

    @Test
    fun `plate in parking records exit successfully`() = runTest {
        val plate = "51A-12345"
        val record = VehicleRecord(1L, plate, System.currentTimeMillis())
        whenever(repository.getActiveRecord(plate)).thenReturn(record)
        whenever(repository.recordExit(plate)).thenReturn(
            Result.success(
                record.copy(exitTime = System.currentTimeMillis(), fee = 5000.0)
            )
        )
        val result = useCase(plate)
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull()?.exitTime)
    }
}