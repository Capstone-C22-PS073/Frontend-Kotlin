package com.capstonec22ps073.toursight.view.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstonec22ps073.toursight.repository.FirstInstallRepository
import kotlinx.coroutines.launch

class OnBoardingViewModel(private val firstInstallRepository: FirstInstallRepository): ViewModel() {
    fun saveUserStatusAsTrue() {
        viewModelScope.launch {
            firstInstallRepository.saveUserStatusAsTrue()
        }
    }
}