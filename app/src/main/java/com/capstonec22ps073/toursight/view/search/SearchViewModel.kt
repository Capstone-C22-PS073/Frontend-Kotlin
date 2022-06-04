package com.capstonec22ps073.toursight.view.search

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.*
import com.capstonec22ps073.toursight.api.CulturalObject
import com.capstonec22ps073.toursight.api.ErrorResponse
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.util.CulturalObjectApplication
import com.capstonec22ps073.toursight.util.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class SearchViewModel(
    app: Application,
    private val authRepository: AuthRepository,
    private val culturalObjectRepository: CulturalObjectRepository
): AndroidViewModel(app) {
    val search: MutableLiveData<Resource<List<CulturalObject>>> = MutableLiveData()

    fun getCulturalObjectBasedOnSearch(token: String, keySearch: String) = viewModelScope.launch {
        safeCulturalObjectsCall(token, keySearch)
    }

    private suspend fun safeCulturalObjectsCall(token: String, keySearch: String) {
        search.postValue(Resource.Loading())

        try {
            if (hasInternetConnection()) {
                val response = culturalObjectRepository.getCulturalObjectBasedOnSearch(token, keySearch)
                search.postValue(handleCulturalObjectResponse(response))
            } else {
                search.postValue(Resource.Error("no internet connection"))
            }

        } catch (t: Throwable) {
            when(t) {
                is IOException -> search.postValue(Resource.Error("network failure"))
                else -> search.postValue(Resource.Error("conversion error"))

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

    fun removeUserDataFromDataStore() = viewModelScope.launch {
        authRepository.removeUserToken()
        authRepository.removeUserEmail()
        authRepository.removeUsername()
    }
}