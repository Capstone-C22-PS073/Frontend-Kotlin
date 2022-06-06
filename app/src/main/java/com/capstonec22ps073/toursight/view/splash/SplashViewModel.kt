package com.capstonec22ps073.toursight.view.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstonec22ps073.toursight.repository.FirstInstallRepository

class SplashViewModel(
    private val firstInstallRepository: FirstInstallRepository
) : ViewModel() {

    fun getUserFirstInstallStatus(): LiveData<Boolean> =
        firstInstallRepository.getUserFirstInstallStatus().asLiveData()

}