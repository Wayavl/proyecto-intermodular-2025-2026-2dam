package com.paralearn.android.domain.use_case.matriculate

import com.paralearn.android.domain.repository.MatriculateRepository
import javax.inject.Inject

class Enroll @Inject constructor(
    private val matriculateRepository: MatriculateRepository
) {
    suspend operator fun invoke(userId: String, courseId: String): Result<Unit> {
        return matriculateRepository.enroll(userId, courseId)
    }
}
