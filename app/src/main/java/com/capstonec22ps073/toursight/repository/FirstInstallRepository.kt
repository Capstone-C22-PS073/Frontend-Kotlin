package com.capstonec22ps073.toursight.repository

import com.capstonec22ps073.toursight.data.FirstInstallDataPreferences

class FirstInstallRepository(private val dataPreferences: FirstInstallDataPreferences) {
    fun getUserFirstInstallStatus() =
        dataPreferences.getUserFirstInstallStatus()

    suspend fun saveUserStatusAsTrue() =
        dataPreferences.saveUserStatusAsTrue()
}