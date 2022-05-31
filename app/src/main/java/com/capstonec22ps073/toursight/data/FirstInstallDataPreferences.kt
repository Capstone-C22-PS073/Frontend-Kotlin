package com.capstonec22ps073.toursight.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirstInstallDataPreferences private constructor(private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>) {
    private val FIRST_INSTALL_KEY = booleanPreferencesKey("is_first_install")

    fun getUserFirstInstallStatus(): Flow<Boolean> {
        return dataStore.data.map { preference ->
            preference[FIRST_INSTALL_KEY] ?: false
        }
    }

    suspend fun saveUserStatusAsTrue() {
        dataStore.edit { preferences ->
            preferences[FIRST_INSTALL_KEY] = true
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: FirstInstallDataPreferences? = null

        fun getInstance(dataStore: DataStore<androidx.datastore.preferences.core.Preferences>): FirstInstallDataPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = FirstInstallDataPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

}