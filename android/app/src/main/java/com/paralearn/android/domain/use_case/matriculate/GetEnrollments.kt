package com.paralearn.android.domain.use_case.matriculate

import com.paralearn.android.domain.model.Enrollment
import com.paralearn.android.domain.repository.MatriculateRepository
import javax.inject.Inject

class GetEnrollments @Inject constructor(
    private val matriculateRepository: MatriculateRepository
) {
    suspend operator fun invoke(userId: String): Result<List<Enrollment>> {
        return matriculateRepository.getEnrollments(userId)
    }
}
