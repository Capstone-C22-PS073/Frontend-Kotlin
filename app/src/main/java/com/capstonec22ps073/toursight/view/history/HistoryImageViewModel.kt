package com.capstonec22ps073.toursight.view.history

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.capstonec22ps073.toursight.api.ErrorResponse
import com.capstonec22ps073.toursight.api.ImageUploadedByUser
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.util.CulturalObjectApplication
import com.capstonec22ps073.toursight.util.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class HistoryImageViewModel(
    app:Application,
    private val authRepository: AuthRepository,
    private val culturalObjectRepository: CulturalObjectRepository
): AndroidViewModel(app) {
    val imagesHistory: MutableLiveData<Resource<List<ImageUploadedByUser>>> = MutableLiveData()

    fun getImageUploadedByUser(token: String, username: String) = viewModelScope.launch {
        safeCulturalObjectsCall(token, username)
    }

    private suspend fun safeCulturalObjectsCall(token: String, username: String) {
        imagesHistory.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()) {
                val response = culturalObjectRepository.getImageUploadedByUser(token, username)
                imagesHistory.postValue(handleImageUploadedByUserResponse(response))
            } else {
                imagesHistory.postValue(Resource.Error("no internet connection"))
            }

        } catch (t: Throwable) {
            when(t) {
                is IOException -> imagesHistory.postValue(Resource.Error("network failure"))
                else -> imagesHistory.postValue(Resource.Error("conversion error"))

            }
        }
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

    fun removeUserDataFromDataStore() = viewModelScope.launch {
        authRepository.removeUserToken()
        authRepository.removeUserEmail()
        authRepository.removeUsername()
    }
}