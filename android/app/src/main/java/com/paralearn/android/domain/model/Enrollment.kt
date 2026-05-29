package com.paralearn.android.domain.model

data class Enrollment(
    val userId: String,
    val courseId: String,
    val beginDate: String,
    val finishDate: String?
)
