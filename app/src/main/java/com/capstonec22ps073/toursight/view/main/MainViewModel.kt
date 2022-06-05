package com.capstonec22ps073.toursight.view.main

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.*
import com.capstonec22ps073.toursight.api.*
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.util.CulturalObjectApplication
import com.capstonec22ps073.toursight.util.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class MainViewModel(
    app: Application,
    private val culturalObjectRepository: CulturalObjectRepository,
    private val authRepository: AuthRepository
) : AndroidViewModel(app) {
    val culturalObjects: MutableLiveData<Resource<List<CulturalObject>>> = MutableLiveData()
    val location: MutableLiveData<String> = MutableLiveData()

    fun setUserLocation(location: String) {
        this.location.postValue(location)
    }

    fun getALlCulturalObjects(token: String) = viewModelScope.launch {
        safeCulturalObjectsCall(token)
    }

    fun getCulturalObjectBasedOnUserLocation(token: String, city: String) = viewModelScope.launch {
        safeCulturalObjectsBasedOnUserLocationCall(token, city)
    }

    private suspend fun safeCulturalObjectsCall(token: String) {
        culturalObjects.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()) {
                val response = culturalObjectRepository.getAllToursight(token)
                culturalObjects.postValue(handleCulturalObjectResponse(response))
            } else {
                culturalObjects.postValue(Resource.Error("no internet connection"))
            }

        } catch (t: Throwable) {
            when(t) {
                is IOException -> culturalObjects.postValue(Resource.Error("network failure"))
                else -> culturalObjects.postValue(Resource.Error("conversion error"))

            }
        }
    }

    private suspend fun safeCulturalObjectsBasedOnUserLocationCall(token: String, city: String) {
        culturalObjects.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()) {
                val response = culturalObjectRepository.getCulturalObjectBasedOnSearch(token, city)
                culturalObjects.postValue(handleCulturalObjectResponse(response))
            } else {
                culturalObjects.postValue(Resource.Error("no internet connection"))
            }

        } catch (t: Throwable) {
            when(t) {
                is IOException -> culturalObjects.postValue(Resource.Error("network failure"))
                else -> culturalObjects.postValue(Resource.Error("conversion error"))

            }
        }
    }

    private fun handleCulturalObjectResponse(response: Response<List<CulturalObject>>): Resource<List<CulturalObject>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        if (response.message() == "Unauthorized") {
            val gson = Gson()
            val type = object : TypeToken<ErrorResponse>() {}.type
            val errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()!!.charStream(), type)
            return Resource.Error(errorResponse?.message!!)
        }
        return Resource.Error(response.message())
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<CulturalObjectApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo.run {
                return when(this?.type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
    }

    fun getUserToken(): LiveData<String> {
        return authRepository.getUserToken()
    }

    fun getUsername(): LiveData<String> {
        return authRepository.getUsername()
    }

    fun getUserEmail(): LiveData<String> {
        return authRepository.getUserEmail()
    }

    fun removeUserToken() = viewModelScope.launch {
        authRepository.removeUserToken()
    }

    fun removeUsername() = viewModelScope.launch {
        authRepository.removeUsername()
    }

    fun removeUserEmail() = viewModelScope.launch {
        authRepository.removeUserEmail()
    }
}