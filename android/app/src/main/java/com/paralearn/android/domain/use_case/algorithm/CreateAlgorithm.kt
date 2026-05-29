package com.paralearn.android.domain.use_case.algorithm

import com.paralearn.android.domain.model.Algorithm
import com.paralearn.android.domain.repository.AlgorithmRepository
import javax.inject.Inject

class CreateAlgorithm @Inject constructor(
    private val algorithmRepository: AlgorithmRepository
) {
    suspend operator fun invoke(algorithm: Algorithm): Result<Unit> {
        return algorithmRepository.createAlgorithm(algorithm)
    }
}
