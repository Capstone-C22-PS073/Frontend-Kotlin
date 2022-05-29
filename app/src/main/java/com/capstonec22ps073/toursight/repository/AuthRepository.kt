package com.capstonec22ps073.toursight.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.capstonec22ps073.toursight.data.AuthDataPreferences

class AuthRepository(private val preferences: AuthDataPreferences) {

    suspend fun saveUserToken(token: String) {
        preferences.saveUserToken(token)
    }

    fun getUserToken(): LiveData<String> {
        return preferences.getUserToken().asLiveData()
    }

    suspend fun removeUserToken() {
        preferences.removeUserToken()
    }

    suspend fun saveUsername(username: String) {
        preferences.saveUsername(username)
    }

    fun getUsername(): LiveData<String> {
        return preferences.getUsername().asLiveData()
    }

    suspend fun removeUsername() {
        preferences.removeUsername()
    }

    suspend fun saveUserEmail(email: String) {
        preferences.saveUserEmail(email)
    }

    fun getUserEmail(): LiveData<String> {
        return preferences.getUserEmail().asLiveData()
    }

    suspend fun removeUserEmail() {
        preferences.removeUserEmail()
    }
}