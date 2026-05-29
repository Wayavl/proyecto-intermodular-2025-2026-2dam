package com.paralearn.android.data.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password_plain: String
)

data class LoginRequest(
    @SerializedName("username") val username: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("password") val password_plain: String
)

data class RegisterResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("user") val user: UserDto?,
    @SerializedName("access_token") val accessToken: String?
)

data class LoginResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("user") val user: UserDto?,
    @SerializedName("access_token") val accessToken: String?
)

data class GetProfileResponse(
    @SerializedName("profile") val profile: UserDto?
)
