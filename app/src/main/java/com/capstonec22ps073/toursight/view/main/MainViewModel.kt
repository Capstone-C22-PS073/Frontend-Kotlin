package com.capstonec22ps073.toursight.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.capstonec22ps073.toursight.repository.AuthRepository

class MainViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun getUserToken(): LiveData<String> {
        return authRepository.getUserToken()
    }
}