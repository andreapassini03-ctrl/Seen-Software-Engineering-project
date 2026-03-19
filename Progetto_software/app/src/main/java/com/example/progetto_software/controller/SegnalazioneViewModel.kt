// package com.example.progetto_software.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Define states for the report submission
sealed class ReportState {
    object Idle : ReportState()
    object Loading : ReportState()
    object Success : ReportState()
    data class Error(val message: String) : ReportState()
}

class SegnalazioneViewModel : ViewModel() {

    private val _reportState = MutableStateFlow<ReportState>(ReportState.Idle)
    val reportState: StateFlow<ReportState> = _reportState.asStateFlow()

    fun submitReport(message: String) {
        if (message.isBlank()) {
            _reportState.value = ReportState.Error("Il messaggio non può essere vuoto.")
            return
        }

        _reportState.value = ReportState.Loading
        viewModelScope.launch {
            try {
                // Simulate a network request or database save
                kotlinx.coroutines.delay(1500) // Simulate network delay

                // TODO: Here you would integrate with your backend/database to send the report
                // Example: myReportRepository.sendReport(message)

                _reportState.value = ReportState.Success
            } catch (e: Exception) {
                _reportState.value = ReportState.Error("Errore durante l'invio: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    fun resetReportState() {
        _reportState.value = ReportState.Idle
    }
}