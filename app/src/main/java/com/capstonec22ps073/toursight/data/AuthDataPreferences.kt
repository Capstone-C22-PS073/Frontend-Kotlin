package com.capstonec22ps073.toursight.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthDataPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val TOKEN_KEY = stringPreferencesKey("token")
    private val USERNAME_KEY = stringPreferencesKey("username")
    private val EMAIL_KEY = stringPreferencesKey("email")

    fun getUserToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    suspend fun saveUserToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun removeUserToken() {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = ""
        }
    }

    fun getUsername(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USERNAME_KEY] ?: ""
        }
    }

    suspend fun saveUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
        }
    }

    suspend fun removeUsername() {
        dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = ""
        }
    }

    fun getUserEmail(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[EMAIL_KEY] ?: ""
        }
    }

    suspend fun saveUserEmail(username: String) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = username
        }
    }

    suspend fun removeUserEmail() {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthDataPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): AuthDataPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthDataPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}