package com.capstonec22ps073.toursight.view.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstonec22ps073.toursight.api.CulturalObject
import com.capstonec22ps073.toursight.api.ErrorResponse
import com.capstonec22ps073.toursight.api.UploadImageResponse
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.util.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class PreviewViewModel(
    private val authRepository: AuthRepository,
    private val culturalObjectRepository: CulturalObjectRepository
): ViewModel() {
    val culturalObject: MutableLiveData<Resource<List<CulturalObject>>> = MutableLiveData()
    val imageUploaded: MutableLiveData<Resource<UploadImageResponse>> = MutableLiveData()

    fun getCulturalObjectByClassname(token: String, classname: String) = viewModelScope.launch {
        culturalObject.postValue(Resource.Loading())
        val response = culturalObjectRepository.getCulturalObjectBasedOnSearch(token,classname)
        culturalObject.postValue(handleCulturalObjectResponse(response))
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

    fun uploadImage(token: String, imageMultipart: MultipartBody.Part, username: RequestBody) = viewModelScope.launch {
        imageUploaded.postValue(Resource.Loading())
        val response = culturalObjectRepository.uploadImage(token,imageMultipart, username)
        imageUploaded.postValue(handleImageUploadResponse(response))
    }

    private fun handleImageUploadResponse(response: Response<UploadImageResponse>): Resource<UploadImageResponse> {
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

    fun removeUserDataFromDataStore() = viewModelScope.launch {
        authRepository.removeUserToken()
        authRepository.removeUserEmail()
        authRepository.removeUsername()
    }

}