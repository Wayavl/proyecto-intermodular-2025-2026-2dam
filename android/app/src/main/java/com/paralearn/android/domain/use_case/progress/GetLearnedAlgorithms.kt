package com.paralearn.android.domain.use_case.progress

import com.paralearn.android.domain.model.Algorithm
import com.paralearn.android.domain.repository.ProgressRepository
import javax.inject.Inject

class GetLearnedAlgorithms @Inject constructor(
    private val progressRepository: ProgressRepository
) {
    suspend operator fun invoke(userId: String, languageCode: String): Result<List<Algorithm>> {
        return progressRepository.getLearnedAlgorithms(userId, languageCode)
    }
}
