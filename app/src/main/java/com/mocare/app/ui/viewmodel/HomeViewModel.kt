package com.mocare.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mocare.app.data.local.entity.MotorEntity
import com.mocare.app.data.repository.MotorRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val motorRepository: MotorRepository) : ViewModel() {
    val motors: StateFlow<List<MotorEntity>> = motorRepository.allMotors
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteMotor(motor: MotorEntity) {
        viewModelScope.launch {
            motorRepository.delete(motor)
        }
    }
}
