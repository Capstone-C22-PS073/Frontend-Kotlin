package com.capstonec22ps073.toursight.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ResponseLogin(

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("accessToken")
    val accessToken: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("username")
    val username: String? = null,

    @field:SerializedName("refreshToken")
    val refreshToken: String? = null,
)

data class ResponseRegister(
    @field:SerializedName("msg")
    val msg: String?
)

@Parcelize
data class CulturalObject(

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("location")
    val location: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("deskripsi")
    val deskripsi: String? = null,

    @field:SerializedName("category")
    val category: String? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("history")
    val history: String? = null
) : Parcelable

data class ErrorResponse(
    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("msg")
    val msg: String? = null
)

data class UploadImageResponse(
    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("uploadedBy")
    val uploadedBy: String? = null,

    @field:SerializedName("image")
    val image: String? = null
)

data class ImageUploadedByUser(
    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("uploadedBy")
    val uploadedBy: String,

    @field:SerializedName("image")
    val image: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String
)
