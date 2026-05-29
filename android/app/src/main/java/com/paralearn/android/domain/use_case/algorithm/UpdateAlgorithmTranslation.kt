package com.paralearn.android.domain.use_case.algorithm

import com.paralearn.android.domain.repository.AlgorithmRepository
import javax.inject.Inject

class UpdateAlgorithmTranslation @Inject constructor(
    private val algorithmRepository: AlgorithmRepository
) {
    suspend operator fun invoke(
        algorithmId: String,
        languageName: String,
        title: String,
        subject: String,
        useCase: String,
        explanation: String
    ): Result<Unit> {
        return algorithmRepository.updateAlgorithmTranslation(
            algorithmId,
            languageName,
            title,
            subject,
            useCase,
            explanation
        )
    }
}
