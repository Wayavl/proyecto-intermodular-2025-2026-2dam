package com.paralearn.android.data.repository

import com.paralearn.android.data.dto.CourseDto
import com.paralearn.android.data.dto.CreateCourseRequest
import com.paralearn.android.data.dto.CreateCourseTranslationRequest
import com.paralearn.android.data.dto.UpdateCoursePremiumRequest
import com.paralearn.android.data.dto.UpdateCourseTranslationRequest
import com.paralearn.android.data.network.CourseAPI
import com.paralearn.android.data.network.apiErrorMessage
import com.paralearn.android.data.session.SessionManager
import com.paralearn.android.domain.model.Course
import com.paralearn.android.domain.repository.CourseRepository
import javax.inject.Inject

class HybridCourseRepository @Inject constructor(
    private val courseAPI: CourseAPI,
    private val sessionManager: SessionManager
) : CourseRepository {

    private fun CourseDto.toDomain(): Course {
        return Course(
            id = this.courseId,
            name = this.name,
            isPremium = this.isPremium,
            description = this.description
        )
    }

    override suspend fun getCourses(): Result<List<Course>> {
        return try {
            val response = courseAPI.getCourses(languageId = sessionManager.languageCode.value)
            val body = response.body()
            if (response.isSuccessful && body?.courses != null) {
                Result.success(body.courses.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.apiErrorMessage("Failed to get courses")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCourse(courseId: String): Result<Course> {
        return try {
            val response = courseAPI.getCourse(courseId, languageId = sessionManager.languageCode.value)
            val body = response.body()
            if (response.isSuccessful && body?.course != null) {
                Result.success(body.course.toDomain())
            } else {
                Result.failure(Exception(response.apiErrorMessage("Failed to get course")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createCourse(name: String, isPremium: Boolean): Result<Unit> {
        return try {
            val response = courseAPI.createCourse(CreateCourseRequest(isPremium))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Create course failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createCourseTranslation(courseId: String, languageName: String, name: String): Result<Unit> {
        return try {
            val response = courseAPI.createCourseTranslation(courseId, CreateCourseTranslationRequest(languageName, name))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Create translation failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCoursePremium(courseId: String, isPremium: Boolean): Result<Unit> {
        return try {
            val response = courseAPI.updateCoursePremium(courseId, UpdateCoursePremiumRequest(isPremium))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Update premium status failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateCourseTranslation(courseId: String, languageName: String, name: String): Result<Unit> {
        return try {
            val response = courseAPI.updateCourseTranslation(courseId, languageName, UpdateCourseTranslationRequest(name))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Update translation failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCourse(courseId: String): Result<Unit> {
        return try {
            val response = courseAPI.deleteCourse(courseId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Delete course failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
