package com.capstonec22ps073.toursight.view.category

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstonec22ps073.toursight.api.CulturalObject
import com.capstonec22ps073.toursight.api.ErrorResponse
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.util.Resource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Response

class CategoryViewModel(private val culturalObjectRepository: CulturalObjectRepository): ViewModel() {
    val culturalObjects: MutableLiveData<Resource<List<CulturalObject>>> = MutableLiveData()

    fun getCulturalObjectsByCategory(token: String, category: String) = viewModelScope.launch {
        culturalObjects.postValue(Resource.Loading())
        val response = culturalObjectRepository.getCulturalObjectsByCategory(token, category)
        culturalObjects.postValue(handleCulturalObjectResponse(response))
    }

    private fun handleCulturalObjectResponse(response: Response<List<CulturalObject>>): Resource<List<CulturalObject>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val gson = Gson()
        val type = object : TypeToken<ErrorResponse>() {}.type
        val errorResponse: ErrorResponse? = gson.fromJson(response.errorBody()!!.charStream(), type)
        return Resource.Error(errorResponse?.message!!)
    }
}