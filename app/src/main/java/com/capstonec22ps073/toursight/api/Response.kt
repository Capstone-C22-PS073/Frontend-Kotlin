package com.capstonec22ps073.toursight.api

import com.google.gson.annotations.SerializedName

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
	val category: String? = null
)

data class ErrorResponse(
	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("msg")
	val msg: String? = null
)
