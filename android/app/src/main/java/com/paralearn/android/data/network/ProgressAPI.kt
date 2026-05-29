package com.paralearn.android.data.network

import com.paralearn.android.data.dto.GetCourseProgressResponse
import com.paralearn.android.data.dto.GetLearnedAlgorithmsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProgressAPI {
    @GET("api/users/{user_id}/learned/algorithms")
    suspend fun getLearnedAlgorithms(
        @Path("user_id") userId: String,
        @Query("lang") languageCode: String
    ): Response<GetLearnedAlgorithmsResponse>

    @GET("api/users/{user_id}/learned/courses")
    suspend fun getCourseProgress(
        @Path("user_id") userId: String,
        @Query("lang") languageCode: String
    ): Response<GetCourseProgressResponse>
}
