package com.paralearn.android.data.network

import com.paralearn.android.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ConfigurationAPI {
    @GET("api/configurations/{user_id}")
    suspend fun getUserConfigurations(
        @Path("user_id") userId: String,
        @Query("lang") languageId: String?
    ): Response<GetUserConfigurationsResponse>

    @POST("api/configurations/{user_id}")
    suspend fun setUserConfiguration(
        @Path("user_id") userId: String,
        @Body request: SetUserConfigurationRequest
    ): Response<MessageResponse>

    @DELETE("api/configurations/{user_id}/{configuration_id}")
    suspend fun resetConfiguration(
        @Path("user_id") userId: String,
        @Path("configuration_id") configurationId: String
    ): Response<MessageResponse>

    @POST("api/configurations")
    suspend fun createConfiguration(@Body request: CreateConfigurationRequest): Response<CreateConfigurationResponse>

    @POST("api/configurations/{configuration_id}/translations")
    suspend fun createConfigurationTranslation(
        @Path("configuration_id") configurationId: String,
        @Body request: CreateConfigurationTranslationRequest
    ): Response<MessageResponse>

    @DELETE("api/configurations/{configuration_id}")
    suspend fun deleteConfiguration(@Path("configuration_id") configurationId: String): Response<MessageResponse>
}
