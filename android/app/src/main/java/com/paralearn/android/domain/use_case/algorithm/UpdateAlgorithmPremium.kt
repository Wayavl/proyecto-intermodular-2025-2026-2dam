package com.paralearn.android.domain.use_case.algorithm

import com.paralearn.android.domain.repository.AlgorithmRepository
import javax.inject.Inject

class UpdateAlgorithmPremium @Inject constructor(
    private val algorithmRepository: AlgorithmRepository
) {
    suspend operator fun invoke(algorithmId: String, isPremium: Boolean): Result<Unit> {
        return algorithmRepository.updateAlgorithmPremium(algorithmId, isPremium)
    }
}
