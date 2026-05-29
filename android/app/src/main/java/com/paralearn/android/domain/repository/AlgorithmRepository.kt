package com.paralearn.android.domain.repository

import com.paralearn.android.domain.model.Algorithm

interface AlgorithmRepository {
    suspend fun executeAlgorithm(algorithmId: String, controls: String): Result<String>
    suspend fun getAlgorithms(): Result<List<Algorithm>>
    suspend fun getAlgorithm(algorithmId: String): Result<Algorithm>
    suspend fun createAlgorithm(algorithm: Algorithm): Result<Unit>
    suspend fun createAlgorithmTranslation(
        algorithmId: String,
        languageName: String,
        title: String,
        subject: String,
        useCase: String,
        explanation: String
    ): Result<Unit>
    suspend fun updateAlgorithmPremium(algorithmId: String, isPremium: Boolean): Result<Unit>
    suspend fun updateAlgorithmControls(algorithmId: String, controls: String): Result<Unit>
    suspend fun updateAlgorithmTranslation(
        algorithmId: String,
        languageName: String,
        title: String,
        subject: String,
        useCase: String,
        explanation: String
    ): Result<Unit>
    suspend fun deleteAlgorithm(algorithmId: String): Result<Unit>
}