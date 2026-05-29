package com.paralearn.android.data.dto

import com.google.gson.annotations.SerializedName

data class CourseDto(
    @SerializedName("course_id") val courseId: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("is_premium") val isPremium: Boolean?,
    @SerializedName("description") val description: String?,
)

data class LessonDto(
    @SerializedName("lesson_id") val lessonId: String?,
    @SerializedName("course_id") val courseId: String?,
    @SerializedName("algorithm_id") val algorithmId: String?,
    @SerializedName("lesson_order") val lessonOrder: Int?,
    @SerializedName("subject") val subject: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("content_md") val contentMd: String?,
    @SerializedName("algorithm") val algorithm: AlgorithmDto?
)

data class GetCoursesResponse(
    @SerializedName("courses") val courses: List<CourseDto>?
)

data class GetCourseResponse(
    @SerializedName("course") val course: CourseDto?
)

data class GetCourseLessonsResponse(
    @SerializedName("lessons") val lessons: List<LessonDto>?
)

data class GetLessonResponse(
    @SerializedName("lesson") val lesson: LessonDto?
)

data class CreateCourseRequest(
    @SerializedName("is_premium") val isPremium: Boolean
)

data class CreateCourseResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("course_id") val courseId: String?
)

data class CreateCourseTranslationRequest(
    @SerializedName("language_name") val languageName: String,
    @SerializedName("name") val name: String
)

data class UpdateCoursePremiumRequest(
    @SerializedName("is_premium") val isPremium: Boolean
)

data class UpdateCourseTranslationRequest(
    @SerializedName("name") val name: String
)

data class CreateLessonRequest(
    @SerializedName("lesson_order") val lessonOrder: Int,
    @SerializedName("algorithm_id") val algorithmId: String?
)

data class CreateLessonResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("lesson_id") val lessonId: String?
)

data class CreateLessonTranslationRequest(
    @SerializedName("language_name") val languageName: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("name") val name: String,
    @SerializedName("content_md") val contentMd: String
)

data class UpdateLessonOrderRequest(
    @SerializedName("new_order") val newOrder: Int
)

data class UpdateLessonAlgorithmRequest(
    @SerializedName("algorithm_id") val algorithmId: String?
)

data class UpdateLessonTranslationRequest(
    @SerializedName("language_name") val languageName: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("name") val name: String,
    @SerializedName("content_md") val contentMd: String
)
