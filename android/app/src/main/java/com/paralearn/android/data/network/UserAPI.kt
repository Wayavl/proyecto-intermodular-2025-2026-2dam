package com.paralearn.android.data.network

import com.paralearn.android.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface UserAPI {
    @POST("api/users/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/users/profile/{username}")
    suspend fun getProfile(@Path("username") username: String): Response<GetProfileResponse>

    @POST("api/users/logout")
    suspend fun logout(): Response<MessageResponse>

    @PUT("api/users/{username}/username")
    suspend fun updateUsername(
        @Path("username") username: String,
        @Body request: UpdateUsernameRequest
    ): Response<MessageResponse>

    @PUT("api/users/{username}/email")
    suspend fun updateEmail(
        @Path("username") username: String,
        @Body request: UpdateEmailRequest
    ): Response<MessageResponse>

    @PUT("api/users/{username}/password")
    suspend fun updatePassword(
        @Path("username") username: String,
        @Body request: UpdatePasswordRequest
    ): Response<MessageResponse>

    @DELETE("api/users/{username}")
    suspend fun deleteUser(@Path("username") username: String): Response<MessageResponse>
}