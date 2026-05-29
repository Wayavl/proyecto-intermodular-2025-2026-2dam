package com.paralearn.android.data.repository

import com.paralearn.android.data.dto.LearnedAlgorithmDto
import com.paralearn.android.data.network.ProgressAPI
import com.paralearn.android.data.network.apiErrorMessage
import com.paralearn.android.domain.model.Algorithm
import com.paralearn.android.domain.model.CourseProgress
import com.paralearn.android.domain.repository.ProgressRepository
import javax.inject.Inject

class HybridProgressRepository @Inject constructor(
    private val progressAPI: ProgressAPI
) : ProgressRepository {

    private fun LearnedAlgorithmDto.toDomain(): Algorithm {
        return Algorithm(
            id = algorithmId,
            title = title,
            subject = subject,
            useCase = useCasesMd,
            explanation = explanationMd,
            controls = controlsYml,
            isPremium = isPremium?.toString()
        )
    }

    override suspend fun getLearnedAlgorithms(
        userId: String,
        languageCode: String
    ): Result<List<Algorithm>> {
        return try {
            val response = progressAPI.getLearnedAlgorithms(userId, languageCode)
            val body = response.body()
            if (response.isSuccessful && body?.algorithms != null) {
                Result.success(body.algorithms.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.apiErrorMessage("Failed to load learned algorithms")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCourseProgress(
        userId: String,
        languageCode: String
    ): Result<List<CourseProgress>> {
        return try {
            val response = progressAPI.getCourseProgress(userId, languageCode)
            val body = response.body()
            if (response.isSuccessful && body?.courses != null) {
                Result.success(
                    body.courses.mapNotNull { dto ->
                        val courseId = dto.courseId ?: return@mapNotNull null
                        CourseProgress(
                            courseId = courseId,
                            courseName = dto.courseName ?: "Course",
                            learnedCount = dto.learnedCount ?: 0,
                            totalCount = dto.totalCount ?: 0,
                            progressPercent = dto.progressPercent ?: 0,
                            isCompleted = dto.isCompleted == true,
                            courseDescription = dto.description ?: ""
                        )
                    }
                )
            } else {
                Result.failure(Exception(response.apiErrorMessage("Failed to load course progress")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
