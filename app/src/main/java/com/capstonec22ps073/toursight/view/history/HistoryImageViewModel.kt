package com.capstonec22ps073.toursight.view.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstonec22ps073.toursight.api.CulturalObject
import com.capstonec22ps073.toursight.api.ErrorResponse
import com.capstonec22ps073.toursight.api.ImageUploadedByUser
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.util.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Response

class HistoryImageViewModel(
    private val authRepository: AuthRepository,
    private val culturalObjectRepository: CulturalObjectRepository
): ViewModel() {
    val imagesHistory: MutableLiveData<Resource<List<ImageUploadedByUser>>> = MutableLiveData()

    fun getImageUploadedByUser(token: String, username: String) = viewModelScope.launch {
        imagesHistory.postValue(Resource.Loading())
        val response = culturalObjectRepository.getImageUploadedByUser(token, username)
        imagesHistory.postValue(handleImageUploadedByUserResponse(response))
    }

    private fun handleImageUploadedByUserResponse(response: Response<List<ImageUploadedByUser>>): Resource<List<ImageUploadedByUser>> {
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

    fun removeUserDataFromDataStore() = viewModelScope.launch {
        authRepository.removeUserToken()
        authRepository.removeUserEmail()
        authRepository.removeUsername()
    }
}