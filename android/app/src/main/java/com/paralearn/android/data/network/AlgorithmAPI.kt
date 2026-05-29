package com.paralearn.android.data.network

import com.paralearn.android.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface AlgorithmAPI {
    @POST("api/algorithms/execute/{algorithm_id}")
    suspend fun executeAlgorithm(
        @Path("algorithm_id") algorithmId: String,
        @Body params: Map<String, @JvmSuppressWildcards Any>
    ): Response<MessageResponse>

    @GET("api/algorithms")
    suspend fun getAlgorithms(@Query("lang") languageId: String?): Response<GetAlgorithmsResponse>

    @GET("api/algorithms/{algorithm_id}")
    suspend fun getAlgorithm(
        @Path("algorithm_id") algorithmId: String,
        @Query("lang") languageId: String?
    ): Response<GetAlgorithmResponse>

    @POST("api/algorithms")
    suspend fun createAlgorithm(@Body request: CreateAlgorithmRequest): Response<CreateAlgorithmResponse>

    @POST("api/algorithms/{algorithm_id}/translations")
    suspend fun createAlgorithmTranslation(
        @Path("algorithm_id") algorithmId: String,
        @Body request: CreateAlgorithmTranslationRequest
    ): Response<MessageResponse>

    @PUT("api/algorithms/{algorithm_id}/premium")
    suspend fun updateAlgorithmPremium(
        @Path("algorithm_id") algorithmId: String,
        @Body request: UpdateAlgorithmPremiumRequest
    ): Response<MessageResponse>

    @PUT("api/algorithms/{algorithm_id}/controls")
    suspend fun updateAlgorithmControls(
        @Path("algorithm_id") algorithmId: String,
        @Body request: UpdateAlgorithmControlsRequest
    ): Response<MessageResponse>

    @PUT("api/algorithms/{algorithm_id}/translations/{language_name}")
    suspend fun updateAlgorithmTranslation(
        @Path("algorithm_id") algorithmId: String,
        @Path("language_name") languageName: String,
        @Body request: UpdateAlgorithmTranslationRequest
    ): Response<MessageResponse>

    @DELETE("api/algorithms/{algorithm_id}")
    suspend fun deleteAlgorithm(@Path("algorithm_id") algorithmId: String): Response<MessageResponse>
}
