package com.paralearn.android.domain.repository

import com.paralearn.android.domain.model.Enrollment

interface MatriculateRepository {
    suspend fun enroll(userId: String, courseId: String): Result<Unit>
    suspend fun getEnrollments(userId: String): Result<List<Enrollment>>
    suspend fun unenroll(userId: String, courseId: String): Result<Unit>
    suspend fun markAsFinished(userId: String, courseId: String): Result<Unit>
}
