package com.nhom.smartparking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom.smartparking.domain.model.VehicleRecord
import com.nhom.smartparking.domain.repository.ParkingRepository
import com.nhom.smartparking.domain.usecase.SyncDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository    : ParkingRepository,
    private val syncUseCase   : SyncDataUseCase
) : ViewModel() {

    val records: StateFlow<List<VehicleRecord>> = repository
        .getAllRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState

    fun sync() = viewModelScope.launch {
        _syncState.value = SyncState.Syncing
        syncUseCase().fold(
            onSuccess = { _syncState.value = SyncState.Success },
            onFailure = { _syncState.value = SyncState.Error(it.message ?: "Lỗi sync") }
        )
        kotlinx.coroutines.delay(2000)
        _syncState.value = SyncState.Idle
    }
}

sealed class SyncState {
    object Idle    : SyncState()
    object Syncing : SyncState()
    object Success : SyncState()
    data class Error(val message: String) : SyncState()
}