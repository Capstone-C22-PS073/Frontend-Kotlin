package com.capstonec22ps073.toursight.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstonec22ps073.toursight.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository): ViewModel() {
    fun saveUserToken(token: String) {
        viewModelScope.launch {
            authRepository.saveUserToken(token)
        }
    }

    fun saveUsername(username: String) {
        viewModelScope.launch {
            authRepository.saveUsername(username)
        }
    }

    fun getUserToken(): LiveData<String> {
        return authRepository.getUserToken()
    }

    fun saveUserEmail(email: String) {
        viewModelScope.launch {
            authRepository.saveUserEmail(email)
        }
    }
}