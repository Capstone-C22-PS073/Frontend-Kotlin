package com.capstonec22ps073.toursight.view.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstonec22ps073.toursight.repository.AuthRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val authRepository: AuthRepository): ViewModel() {
    fun removeUserToken() = viewModelScope.launch {
        authRepository.removeUserToken()
    }

    fun removeUsername() = viewModelScope.launch {
        authRepository.removeUsername()
    }

    fun removeUserEmail() = viewModelScope.launch {
        authRepository.removeUserEmail()
    }
}