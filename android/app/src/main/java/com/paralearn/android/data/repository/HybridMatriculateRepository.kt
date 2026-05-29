package com.paralearn.android.data.repository

import com.paralearn.android.data.dto.*
import com.paralearn.android.data.network.MatriculateAPI
import com.paralearn.android.data.network.apiErrorMessage
import com.paralearn.android.domain.model.Enrollment
import com.paralearn.android.domain.repository.MatriculateRepository
import javax.inject.Inject

class HybridMatriculateRepository @Inject constructor(
    private val matriculateAPI: MatriculateAPI
) : MatriculateRepository {

    private fun EnrollmentDto.toDomain(): Enrollment {
        return Enrollment(
            userId = this.userId ?: "",
            courseId = this.courseId ?: "",
            beginDate = this.beginDate ?: "",
            finishDate = this.finishDate
        )
    }

    override suspend fun enroll(userId: String, courseId: String): Result<Unit> {
        return try {
            val response = matriculateAPI.enroll(EnrollRequest(userId, courseId))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Enroll failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEnrollments(userId: String): Result<List<Enrollment>> {
        return try {
            val response = matriculateAPI.getEnrollments(userId)
            val body = response.body()
            if (response.isSuccessful && body?.enrollments != null) {
                Result.success(body.enrollments.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.apiErrorMessage("Failed to get enrollments")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unenroll(userId: String, courseId: String): Result<Unit> {
        return try {
            val response = matriculateAPI.unenroll(userId, courseId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Unenroll failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsFinished(userId: String, courseId: String): Result<Unit> {
        return try {
            val response = matriculateAPI.markAsFinished(userId, courseId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Mark as finished failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
