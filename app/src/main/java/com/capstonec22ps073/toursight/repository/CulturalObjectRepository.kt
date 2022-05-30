package com.capstonec22ps073.toursight.repository

import com.capstonec22ps073.toursight.api.ApiConfig

class CulturalObjectRepository() {
    suspend fun getAllToursight(token: String) =
        ApiConfig.getApiService().getAllToursight(token = "Bearer $token")
}