package com.capstonec22ps073.toursight.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstonec22ps073.toursight.api.CulturalObject
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class HomeViewModel(
    private val culturalObjectRepository: CulturalObjectRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
//    val toursights: MutableLiveData<Resource<List<CulturalObject>>> = MutableLiveData()
//
//    fun getALlToursight(token: String) = viewModelScope.launch {
//        toursights.postValue(Resource.Loading())
//        val response = culturalObjectRepository.getAllToursight(token)
//        toursights.postValue(handleCulturalObjectResponse(response))
//    }
//
//    private fun handleCulturalObjectResponse(response: Response<List<CulturalObject>>): Resource<List<CulturalObject>> {
//        if (response.isSuccessful) {
//            response.body()?.let { resultResponse ->
//                return Resource.Success(resultResponse)
//            }
//        }
//        return Resource.Error(response.message())
//    }
//
//    fun getUserToken(): LiveData<String> {
//        return authRepository.getUserToken()
//    }
}