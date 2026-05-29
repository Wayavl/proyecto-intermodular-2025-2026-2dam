package com.paralearn.android.data.dto

import com.google.gson.annotations.SerializedName

data class LearnedAlgorithmDto(
    @SerializedName("algorithm_id") val algorithmId: String?,
    @SerializedName("finish_date") val finishDate: String?,
    @SerializedName("is_premium") val isPremium: Boolean?,
    @SerializedName("controls_yml") val controlsYml: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("subject") val subject: String?,
    @SerializedName("explanation_md") val explanationMd: String?,
    @SerializedName("use_cases_md") val useCasesMd: String?
)

data class GetLearnedAlgorithmsResponse(
    @SerializedName("algorithms") val algorithms: List<LearnedAlgorithmDto>?
)

data class LearnedLessonDto(
    @SerializedName("lesson_id") val lessonId: String?,
    @SerializedName("course_id") val courseId: String?,
    @SerializedName("finish_date") val finishDate: String?,
    @SerializedName("lesson_name") val lessonName: String?,
    @SerializedName("course_name") val courseName: String?
)

data class GetLearnedLessonsResponse(
    @SerializedName("lessons") val lessons: List<LearnedLessonDto>?
)

data class CourseProgressDto(
    @SerializedName("course_id") val courseId: String?,
    @SerializedName("course_name") val courseName: String?,
    @SerializedName("learned_count") val learnedCount: Int?,
    @SerializedName("total_count") val totalCount: Int?,
    @SerializedName("progress_percent") val progressPercent: Int?,
    @SerializedName("is_completed") val isCompleted: Boolean?,
    @SerializedName("is_completed") val description: String?
)

data class GetCourseProgressResponse(
    @SerializedName("courses") val courses: List<CourseProgressDto>?
)
