package com.capstonec22ps073.toursight.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.view.login.LoginViewModel
import com.capstonec22ps073.toursight.view.main.MainViewModel
import com.capstonec22ps073.toursight.view.profile.ProfileViewModel

class AuthViewModelFactory(private val authRepository: AuthRepository): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authRepository) as T
        } else if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(authRepository) as T
        } else if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}