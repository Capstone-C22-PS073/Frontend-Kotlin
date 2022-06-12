package com.capstonec22ps073.toursight.repository

import com.capstonec22ps073.toursight.api.ApiConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CulturalObjectRepository {
    suspend fun getAllToursight(token: String) =
        ApiConfig.getApiService().getAllToursight(token = "Bearer $token")

    suspend fun getCulturalObjectsByCategory(token: String, category: String) =
        ApiConfig.getApiService()
            .getListCulturalObjectByCategory(token = "Bearer $token", category = category)

    suspend fun getCulturalObjectBasedOnSearch(token: String, keySearch: String) =
        ApiConfig.getApiService().getListCulturalObjectBySearch("Bearer $token", keySearch)

    suspend fun getCulturalObjectByClassname(token: String, classname: String) =
        ApiConfig.getApiService().getCulturalObjectByClassname("Bearer $token", classname)

    suspend fun uploadImage(token: String, imageMultipart: MultipartBody.Part, username: RequestBody) =
        ApiConfig.getApiService().uploadImage("Bearer $token", username, imageMultipart)

    suspend fun getImageUploadedByUser(token: String, username: String) =
        ApiConfig.getApiService().getListImageUploadedByUser("Bearer $token", username)
}