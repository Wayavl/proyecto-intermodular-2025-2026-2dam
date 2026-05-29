package com.paralearn.android.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paralearn.android.data.dto.AlgorithmDto
import com.paralearn.android.data.dto.CreateAlgorithmRequest
import com.paralearn.android.data.dto.CreateAlgorithmTranslationRequest
import com.paralearn.android.data.dto.UpdateAlgorithmControlsRequest
import com.paralearn.android.data.dto.UpdateAlgorithmPremiumRequest
import com.paralearn.android.data.dto.UpdateAlgorithmTranslationRequest
import com.paralearn.android.data.network.AlgorithmAPI
import com.paralearn.android.data.network.apiErrorMessage
import com.paralearn.android.data.session.SessionManager
import com.paralearn.android.domain.model.Algorithm
import com.paralearn.android.domain.repository.AlgorithmRepository
import javax.inject.Inject

class HybridAlgorithmRepository @Inject constructor(
    private val algorithmAPI: AlgorithmAPI,
    private val sessionManager: SessionManager
) : AlgorithmRepository {

    private fun AlgorithmDto.toDomain(): Algorithm {
        return Algorithm(
            title = this.title,
            subject = this.subject,
            useCase = this.useCasesMd,
            explanation = this.explanationMd,
            controls = this.controlsJson ?: this.controlsYml,
            isPremium = this.isPremium?.toString(),
            id = this.algorithmId
        )
    }

    override suspend fun executeAlgorithm(algorithmId: String, controls: String): Result<String> {
        return try {
            val paramsMap: Map<String, Any> = try {
                Gson().fromJson(
                    controls,
                    object : TypeToken<Map<String, Any>>() {}.type
                )
            } catch (e: Exception) {
                emptyMap()
            }
            val response = algorithmAPI.executeAlgorithm(algorithmId, paramsMap)
            val body = response.body()
            if (response.isSuccessful) {
                Result.success(body?.message ?: "Execution successful")
            } else {
                Result.failure(Exception(response.apiErrorMessage("Execution failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAlgorithms(): Result<List<Algorithm>> {
        return try {
            val response = algorithmAPI.getAlgorithms(languageId = sessionManager.languageCode.value)
            val body = response.body()
            if (response.isSuccessful && body?.algorithms != null) {
                Result.success(body.algorithms.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.apiErrorMessage("Failed to get algorithms")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAlgorithm(algorithmId: String): Result<Algorithm> {
        return try {
            val response = algorithmAPI.getAlgorithm(algorithmId, languageId = sessionManager.languageCode.value)
            val body = response.body()
            if (response.isSuccessful && body?.algorithm != null) {
                Result.success(body.algorithm.toDomain())
            } else {
                Result.failure(Exception(response.apiErrorMessage("Failed to get algorithm")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createAlgorithm(algorithm: Algorithm): Result<Unit> {
        return try {
            val response = algorithmAPI.createAlgorithm(
                CreateAlgorithmRequest(
                    isPremium = algorithm.isPremium?.toBoolean() ?: false,
                    controlsYml = algorithm.controls ?: ""
                )
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Create algorithm failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createAlgorithmTranslation(
        algorithmId: String,
        languageName: String,
        title: String,
        subject: String,
        useCase: String,
        explanation: String
    ): Result<Unit> {
        return try {
            val response = algorithmAPI.createAlgorithmTranslation(
                algorithmId,
                CreateAlgorithmTranslationRequest(
                    languageName = languageName,
                    title = title,
                    subject = subject,
                    explanationMd = explanation,
                    useCasesMd = useCase
                )
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Create translation failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAlgorithmPremium(algorithmId: String, isPremium: Boolean): Result<Unit> {
        return try {
            val response = algorithmAPI.updateAlgorithmPremium(algorithmId, UpdateAlgorithmPremiumRequest(isPremium))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Update premium status failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAlgorithmControls(algorithmId: String, controls: String): Result<Unit> {
        return try {
            val response = algorithmAPI.updateAlgorithmControls(algorithmId, UpdateAlgorithmControlsRequest(controls))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Update controls failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAlgorithmTranslation(
        algorithmId: String,
        languageName: String,
        title: String,
        subject: String,
        useCase: String,
        explanation: String
    ): Result<Unit> {
        return try {
            val response = algorithmAPI.updateAlgorithmTranslation(
                algorithmId,
                languageName,
                UpdateAlgorithmTranslationRequest(
                    title = title,
                    subject = subject,
                    explanationMd = explanation,
                    useCasesMd = useCase
                )
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Update translation failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAlgorithm(algorithmId: String): Result<Unit> {
        return try {
            val response = algorithmAPI.deleteAlgorithm(algorithmId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Delete algorithm failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
