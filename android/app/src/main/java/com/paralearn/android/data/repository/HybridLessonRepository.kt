package com.paralearn.android.data.repository

import com.paralearn.android.data.dto.*
import com.paralearn.android.data.network.CourseAPI
import com.paralearn.android.data.network.apiErrorMessage
import com.paralearn.android.data.session.SessionManager
import com.paralearn.android.domain.model.Algorithm
import com.paralearn.android.domain.model.Lesson
import com.paralearn.android.domain.repository.LessonRepository
import javax.inject.Inject

class HybridLessonRepository @Inject constructor(
    private val courseAPI: CourseAPI,
    private val sessionManager: SessionManager
) : LessonRepository {

    private fun LessonDto.toDomain(): Lesson {
        return Lesson(
            id = this.lessonId,
            courseId = this.courseId,
            algorithmId = this.algorithmId,
            name = this.name,
            subject = this.subject,
            contentMD = this.contentMd,
            order = this.lessonOrder,
            algorithm = this.algorithm?.let {
                Algorithm(
                    title = it.title,
                    subject = it.subject,
                    useCase = it.useCasesMd,
                    explanation = it.explanationMd,
                    controls = it.controlsYml,
                    isPremium = it.isPremium?.toString(),
                    id = it.algorithmId
                )
            }
        )
    }

    override suspend fun getCourseLessons(courseId: String): Result<List<Lesson>> {
        return try {
            val response = courseAPI.getCourseLessons(courseId, languageId = sessionManager.languageCode.value)
            val body = response.body()
            if (response.isSuccessful && body?.lessons != null) {
                Result.success(body.lessons.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.apiErrorMessage("Failed to get course lessons")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLesson(lessonId: String): Result<Lesson> {
        return try {
            val response = courseAPI.getLesson(lessonId, languageId = sessionManager.languageCode.value)
            val body = response.body()
            if (response.isSuccessful && body?.lesson != null) {
                Result.success(body.lesson.toDomain())
            } else {
                Result.failure(Exception(response.apiErrorMessage("Failed to get lesson")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createLesson(courseId: String, lesson: Lesson): Result<Unit> {
        return try {
            val response = courseAPI.createLesson(
                courseId,
                CreateLessonRequest(
                    lessonOrder = lesson.order ?: 0,
                    algorithmId = lesson.algorithmId
                )
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Create lesson failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createLessonTranslation(
        lessonId: String,
        languageName: String,
        name: String,
        subject: String,
        contentMD: String
    ): Result<Unit> {
        return try {
            val response = courseAPI.createLessonTranslation(
                lessonId,
                CreateLessonTranslationRequest(
                    languageName = languageName,
                    name = name,
                    subject = subject,
                    contentMd = contentMD
                )
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Create lesson translation failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLessonOrder(lessonId: String, order: Int): Result<Unit> {
        return try {
            val response = courseAPI.updateLessonOrder(lessonId, UpdateLessonOrderRequest(order))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Update lesson order failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLessonAlgorithm(lessonId: String, algorithmId: String): Result<Unit> {
        return try {
            val response = courseAPI.updateLessonAlgorithm(lessonId, UpdateLessonAlgorithmRequest(algorithmId))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Update lesson algorithm failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLessonTranslation(
        lessonId: String,
        languageName: String,
        name: String,
        subject: String,
        contentMD: String
    ): Result<Unit> {
        return try {
            val response = courseAPI.updateLessonTranslation(
                lessonId,
                languageName,
                UpdateLessonTranslationRequest(
                    languageName = languageName,
                    name = name,
                    subject = subject,
                    contentMd = contentMD
                )
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Update lesson translation failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteLesson(lessonId: String): Result<Unit> {
        return try {
            val response = courseAPI.deleteLesson(lessonId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Delete lesson failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
