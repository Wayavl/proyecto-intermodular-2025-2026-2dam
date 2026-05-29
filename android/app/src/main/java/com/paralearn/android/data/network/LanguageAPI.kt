package com.paralearn.android.data.network

import com.paralearn.android.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface LanguageAPI {
    @GET("api/languages")
    suspend fun getLanguages(): Response<GetLanguagesResponse>

    @POST("api/languages")
    suspend fun createLanguage(@Body request: CreateLanguageRequest): Response<MessageResponse>

    @DELETE("api/languages/{name}")
    suspend fun deleteLanguage(@Path("name") name: String): Response<MessageResponse>
}
