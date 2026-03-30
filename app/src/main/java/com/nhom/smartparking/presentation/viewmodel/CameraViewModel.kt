package com.nhom.smartparking.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom.smartparking.domain.usecase.RecordEntryUseCase
import com.nhom.smartparking.domain.usecase.RecordExitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val recordEntryUseCase : RecordEntryUseCase,
    private val recordExitUseCase  : RecordExitUseCase
) : ViewModel() {

    private val _detectedPlate = MutableStateFlow<String?>(null)
    val detectedPlate: StateFlow<String?> = _detectedPlate

    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Idle)
    val uiState: StateFlow<CameraUiState> = _uiState

    fun onPlateDetected(plate: String) {
        if (_detectedPlate.value != plate) _detectedPlate.value = plate
    }

    fun recordEntry(plate: String) = viewModelScope.launch {
        _uiState.value = CameraUiState.Loading
        recordEntryUseCase(plate).fold(
            onSuccess = { _uiState.value = CameraUiState.EntrySuccess(it.licensePlate) },
            onFailure = { _uiState.value = CameraUiState.Error(it.message ?: "Lỗi") }
        )
    }

    fun recordExit(plate: String) = viewModelScope.launch {
        _uiState.value = CameraUiState.Loading
        recordExitUseCase(plate).fold(
            onSuccess = { _uiState.value = CameraUiState.ExitSuccess(it.licensePlate, it.fee ?: 0.0) },
            onFailure = { _uiState.value = CameraUiState.Error(it.message ?: "Lỗi") }
        )
    }

    fun resetState() { _uiState.value = CameraUiState.Idle }
    fun clearPlate() { _detectedPlate.value = null }
}

sealed class CameraUiState {
    object Idle    : CameraUiState()
    object Loading : CameraUiState()
    data class EntrySuccess(val plate: String) : CameraUiState()
    data class ExitSuccess(val plate: String, val fee: Double) : CameraUiState()
    data class Error(val message: String) : CameraUiState()
}