package com.paralearn.android.domain.model

data class Lesson(
    val id: String?,
    val courseId: String?,
    val algorithmId: String?,
    val name: String?,
    val subject: String?,
    val contentMD: String?,
    val order: Int?,
    val algorithm: Algorithm?
)
