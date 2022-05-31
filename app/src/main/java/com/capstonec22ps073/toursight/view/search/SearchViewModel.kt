package com.capstonec22ps073.toursight.view.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstonec22ps073.toursight.api.CulturalObject
import com.capstonec22ps073.toursight.api.ErrorResponse
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.util.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Response

class SearchViewModel(
    private val authRepository: AuthRepository,
    private val culturalObjectRepository: CulturalObjectRepository
): ViewModel() {
    val search: MutableLiveData<Resource<List<CulturalObject>>> = MutableLiveData()

    fun getCulturalObjectBasedOnSearch(token: String, keySearch: String) = viewModelScope.launch {
        search.postValue(Resource.Loading())
        val response = culturalObjectRepository.getCulturalObjectBasedOnSearch(token, keySearch)
        search.postValue(handleCulturalObjectResponse(response))
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

    fun removeUserDataFromDataStore() = viewModelScope.launch {
        authRepository.removeUserToken()
        authRepository.removeUserEmail()
        authRepository.removeUsername()
    }
}