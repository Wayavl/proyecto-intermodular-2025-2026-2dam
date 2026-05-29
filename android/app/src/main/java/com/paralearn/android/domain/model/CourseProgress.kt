package com.paralearn.android.domain.model

data class CourseProgress(
    val courseId: String,
    val courseName: String,
    val courseDescription: String,
    val learnedCount: Int,
    val totalCount: Int,
    val progressPercent: Int,
    val isCompleted: Boolean
)
