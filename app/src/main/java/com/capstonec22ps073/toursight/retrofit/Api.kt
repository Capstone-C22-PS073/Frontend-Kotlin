package com.capstonec22ps073.toursight.retrofit

import com.capstonec22ps073.toursight.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Api {
    private val baseUrl = "https://belajarteknologi.space/api\n"

    fun getAPIService(): APIService {
        val loggingInterceptor = if (BuildConfig.DEBUG) {

        }
        else {

        }



        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())

            .build()

        return retrofit.create(APIService::class.java)
    }
}
