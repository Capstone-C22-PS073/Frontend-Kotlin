package com.capstonec22ps073.toursight.view.main

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstonec22ps073.toursight.api.*
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.util.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(
    private val culturalObjectRepository: CulturalObjectRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    val culturalObjects: MutableLiveData<Resource<List<CulturalObject>>> = MutableLiveData()
    val location: MutableLiveData<Location> = MutableLiveData()

    fun setLocation(location: Location) {
        this.location.postValue(location)
    }

    fun getALlCulturalObjects(token: String) = viewModelScope.launch {
        culturalObjects.postValue(Resource.Loading())
        val response = culturalObjectRepository.getAllToursight(token)
        culturalObjects.postValue(handleCulturalObjectResponse(response))
    }

    fun getCulturalObjectBasedOnUserLocation(token: String, city: String) = viewModelScope.launch {
        culturalObjects.postValue(Resource.Loading())
        val response = culturalObjectRepository.getCulturalObjectBasedOnSearch(token, city)
        culturalObjects.postValue(handleCulturalObjectResponse(response))
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