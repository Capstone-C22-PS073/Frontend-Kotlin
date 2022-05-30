package com.capstonec22ps073.toursight.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstonec22ps073.toursight.repository.FirstInstallRepository
import com.capstonec22ps073.toursight.view.onboarding.OnBoardingViewModel
import com.capstonec22ps073.toursight.view.splash.SplashViewModel

class MainViewModelFactory(private val firstInstallRepository: FirstInstallRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(firstInstallRepository) as T
        } else if (modelClass.isAssignableFrom(OnBoardingViewModel::class.java)) {
            return OnBoardingViewModel(firstInstallRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}