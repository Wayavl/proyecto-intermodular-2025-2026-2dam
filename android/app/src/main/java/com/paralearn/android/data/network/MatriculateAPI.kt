package com.paralearn.android.data.network

import com.paralearn.android.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface MatriculateAPI {
    @POST("api/matriculate/enroll")
    suspend fun enroll(@Body request: EnrollRequest): Response<MessageResponse>

    @GET("api/matriculate/{user_id}")
    suspend fun getEnrollments(@Path("user_id") userId: String): Response<GetEnrollmentsResponse>

    @DELETE("api/matriculate/{user_id}/{course_id}")
    suspend fun unenroll(
        @Path("user_id") userId: String,
        @Path("course_id") courseId: String
    ): Response<MessageResponse>

    @POST("api/matriculate/{user_id}/{course_id}/finish")
    suspend fun markAsFinished(
        @Path("user_id") userId: String,
        @Path("course_id") courseId: String
    ): Response<MessageResponse>
}
