package com.paralearn.android.data.network

import com.paralearn.android.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface CourseAPI {
    @GET("api/courses")
    suspend fun getCourses(@Query("lang") languageId: String?): Response<GetCoursesResponse>

    @GET("api/courses/{course_id}")
    suspend fun getCourse(
        @Path("course_id") courseId: String,
        @Query("lang") languageId: String?
    ): Response<GetCourseResponse>

    @GET("api/courses/{course_id}/lessons")
    suspend fun getCourseLessons(
        @Path("course_id") courseId: String,
        @Query("lang") languageId: String?
    ): Response<GetCourseLessonsResponse>

    @GET("api/courses/lessons/{lesson_id}")
    suspend fun getLesson(
        @Path("lesson_id") lessonId: String,
        @Query("lang") languageId: String?
    ): Response<GetLessonResponse>

    @POST("api/courses")
    suspend fun createCourse(@Body request: CreateCourseRequest): Response<CreateCourseResponse>

    @POST("api/courses/{course_id}/translations")
    suspend fun createCourseTranslation(
        @Path("course_id") courseId: String,
        @Body request: CreateCourseTranslationRequest
    ): Response<MessageResponse>

    @PUT("api/courses/{course_id}/premium")
    suspend fun updateCoursePremium(
        @Path("course_id") courseId: String,
        @Body request: UpdateCoursePremiumRequest
    ): Response<MessageResponse>

    @PUT("api/courses/{course_id}/translations/{language_name}")
    suspend fun updateCourseTranslation(
        @Path("course_id") courseId: String,
        @Path("language_name") languageName: String,
        @Body request: UpdateCourseTranslationRequest
    ): Response<MessageResponse>

    @DELETE("api/courses/{course_id}")
    suspend fun deleteCourse(@Path("course_id") courseId: String): Response<MessageResponse>

    @POST("api/courses/{course_id}/lessons")
    suspend fun createLesson(
        @Path("course_id") courseId: String,
        @Body request: CreateLessonRequest
    ): Response<CreateLessonResponse>

    @POST("api/courses/lessons/{lesson_id}/translations")
    suspend fun createLessonTranslation(
        @Path("lesson_id") lessonId: String,
        @Body request: CreateLessonTranslationRequest
    ): Response<MessageResponse>

    @PUT("api/courses/lessons/{lesson_id}/order")
    suspend fun updateLessonOrder(
        @Path("lesson_id") lessonId: String,
        @Body request: UpdateLessonOrderRequest
    ): Response<MessageResponse>

    @PUT("api/courses/lessons/{lesson_id}/algorithm")
    suspend fun updateLessonAlgorithm(
        @Path("lesson_id") lessonId: String,
        @Body request: UpdateLessonAlgorithmRequest
    ): Response<MessageResponse>

    @PUT("api/courses/lessons/{lesson_id}/translations/{language_name}")
    suspend fun updateLessonTranslation(
        @Path("lesson_id") lessonId: String,
        @Path("language_name") languageName: String,
        @Body request: UpdateLessonTranslationRequest
    ): Response<MessageResponse>

    @DELETE("api/courses/lessons/{lesson_id}")
    suspend fun deleteLesson(@Path("lesson_id") lessonId: String): Response<MessageResponse>
}
