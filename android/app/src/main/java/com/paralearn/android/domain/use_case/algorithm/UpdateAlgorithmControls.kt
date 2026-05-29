package com.paralearn.android.domain.use_case.algorithm

import com.paralearn.android.domain.repository.AlgorithmRepository
import javax.inject.Inject

class UpdateAlgorithmControls @Inject constructor(
    private val algorithmRepository: AlgorithmRepository
) {
    suspend operator fun invoke(algorithmId: String, controls: String): Result<Unit> {
        return algorithmRepository.updateAlgorithmControls(algorithmId, controls)
    }
}
