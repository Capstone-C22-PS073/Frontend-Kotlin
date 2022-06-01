package com.capstonec22ps073.toursight.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("users")
    fun register(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ResponseRegister>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ResponseLogin>

    @GET("toursight")
    suspend fun getAllToursight(
        @Header("Authorization") token: String,
    ): Response<List<CulturalObject>>

    @GET("category")
    suspend fun getListCulturalObjectByCategory(
        @Header("Authorization") token: String,
        @Query("category") category: String,
    ): Response<List<CulturalObject>>

    @GET("search")
    suspend fun getListCulturalObjectBySearch(
        @Header("Authorization") token: String,
        @Query("keyword") keyword: String,
    ): Response<List<CulturalObject>>

    @FormUrlEncoded
    @GET("getdatabyimg")
    suspend fun getCulturalObjectByClassname(
        @Header("Authorization") token: String,
        @Field("classname") classname: String,
    ): Response<List<CulturalObject>>

    @Multipart
    @POST("image")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part("username") username: RequestBody,
        @Part image: MultipartBody.Part,
    ): Response<UploadImageResponse>
}