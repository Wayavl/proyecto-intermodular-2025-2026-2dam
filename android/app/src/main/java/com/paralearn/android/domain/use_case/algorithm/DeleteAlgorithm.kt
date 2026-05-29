package com.paralearn.android.domain.use_case.algorithm

import com.paralearn.android.domain.repository.AlgorithmRepository
import javax.inject.Inject

class DeleteAlgorithm @Inject constructor(
    private val algorithmRepository: AlgorithmRepository
) {
    suspend operator fun invoke(algorithmId: String): Result<Unit> {
        return algorithmRepository.deleteAlgorithm(algorithmId)
    }
}
