package com.paralearn.android.domain.use_case.algorithm

import com.paralearn.android.domain.model.Algorithm
import com.paralearn.android.domain.repository.AlgorithmRepository
import javax.inject.Inject

class GetAlgorithm @Inject constructor(
    private val algorithmRepository: AlgorithmRepository
) {
    suspend operator fun invoke(algorithmId: String): Result<Algorithm> {
        return algorithmRepository.getAlgorithm(algorithmId)
    }
}
