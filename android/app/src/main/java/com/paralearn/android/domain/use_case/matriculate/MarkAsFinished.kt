package com.paralearn.android.domain.use_case.matriculate

import com.paralearn.android.domain.repository.MatriculateRepository
import javax.inject.Inject

class MarkAsFinished @Inject constructor(
    private val matriculateRepository: MatriculateRepository
) {
    suspend operator fun invoke(userId: String, courseId: String): Result<Unit> {
        return matriculateRepository.markAsFinished(userId, courseId)
    }
}
