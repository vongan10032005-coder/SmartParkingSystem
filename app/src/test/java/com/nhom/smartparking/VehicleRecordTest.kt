package com.nhom.smartparking

import com.nhom.smartparking.domain.model.VehicleRecord
import org.junit.Assert.*
import org.junit.Test

class VehicleRecordTest {

    @Test
    fun `isParked returns true when exitTime is null`() {
        val record = VehicleRecord(
            id = 1L,
            licensePlate = "51A-12345",
            entryTime = System.currentTimeMillis()
        )
        assertTrue(record.isParked())
    }

    @Test
    fun `isParked returns false when exitTime is set`() {
        val record = VehicleRecord(
            id = 1L,
            licensePlate = "51A-12345",
            entryTime = System.currentTimeMillis() - 3600000,
            exitTime = System.currentTimeMillis()
        )
        assertFalse(record.isParked())
    }

    @Test
    fun `formattedFee returns correct string`() {
        val record = VehicleRecord(
            id = 1L,
            licensePlate = "51A-12345",
            entryTime = System.currentTimeMillis(),
            fee = 15000.0
        )
        assertEquals("15000đ", record.formattedFee())
    }

    @Test
    fun `formattedFee returns chua tinh when null`() {
        val record = VehicleRecord(
            id = 1L,
            licensePlate = "51A-12345",
            entryTime = System.currentTimeMillis()
        )
        assertEquals("Chưa tính", record.formattedFee())
    }

    @Test
    fun `durationMinutes calculates correctly`() {
        val entry = System.currentTimeMillis() - 60_000 * 30
        val record = VehicleRecord(
            id = 1L,
            licensePlate = "51A-12345",
            entryTime = entry
        )
        assertTrue(record.durationMinutes() in 29..31)
    }
}