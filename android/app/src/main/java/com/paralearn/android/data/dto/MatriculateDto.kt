package com.paralearn.android.data.dto

import com.google.gson.annotations.SerializedName

data class EnrollmentDto(
    @SerializedName("user_id") val userId: String?,
    @SerializedName("course_id") val courseId: String?,
    @SerializedName("begin_date") val beginDate: String?,
    @SerializedName("finish_date") val finishDate: String?
)

data class EnrollRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("course_id") val courseId: String
)

data class GetEnrollmentsResponse(
    @SerializedName("enrollments") val enrollments: List<EnrollmentDto>?
)
