package com.capstonec22ps073.toursight.view.preview

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.*
import com.capstonec22ps073.toursight.api.CulturalObject
import com.capstonec22ps073.toursight.api.ErrorResponse
import com.capstonec22ps073.toursight.api.UploadImageResponse
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.util.CulturalObjectApplication
import com.capstonec22ps073.toursight.util.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.IOException

class PreviewViewModel(
    app: Application,
    private val authRepository: AuthRepository,
    private val culturalObjectRepository: CulturalObjectRepository
): AndroidViewModel(app) {
    val culturalObject: MutableLiveData<Resource<List<CulturalObject>>> = MutableLiveData()
    val imageUploaded: MutableLiveData<Resource<UploadImageResponse>> = MutableLiveData()

    fun getCulturalObjectByClassname(token: String, classname: String) = viewModelScope.launch {
        safeCulturalObjectsCall(token, classname)
    }

    private suspend fun safeCulturalObjectsCall(token: String, classname: String) {
        culturalObject.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()) {
                val response = culturalObjectRepository.getCulturalObjectBasedOnSearch(token, classname)
                culturalObject.postValue(handleCulturalObjectResponse(response))
            } else {
                culturalObject.postValue(Resource.Error("no internet connection"))
            }

        } catch (t: Throwable) {
            when(t) {
                is IOException -> culturalObject.postValue(Resource.Error("network failure"))
                else -> culturalObject.postValue(Resource.Error("conversion error"))

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

    fun uploadImage(token: String, imageMultipart: MultipartBody.Part, username: RequestBody) = viewModelScope.launch {
        safeUploadImageCall(token, imageMultipart, username)
    }

    private suspend fun safeUploadImageCall(token: String, imageMultipart: MultipartBody.Part, username: RequestBody) {
        imageUploaded.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()) {
                val response = culturalObjectRepository.uploadImage(token, imageMultipart, username)
                imageUploaded.postValue(handleImageUploadResponse(response))
            } else {
                imageUploaded.postValue(Resource.Error("no internet connection"))
            }

        } catch (t: Throwable) {
            when(t) {
                is IOException -> imageUploaded.postValue(Resource.Error("network failure"))
                else -> imageUploaded.postValue(Resource.Error("conversion error"))

            }
        }
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

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<CulturalObjectApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo.run {
                return when(this?.type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
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

    fun removeUserDataFromDataStore() = viewModelScope.launch {
        authRepository.removeUserToken()
        authRepository.removeUserEmail()
        authRepository.removeUsername()
    }

}