package com.paralearn.android.domain.use_case.progress

import com.paralearn.android.domain.model.CourseProgress
import com.paralearn.android.domain.repository.ProgressRepository
import javax.inject.Inject

class GetCourseProgress @Inject constructor(
    private val progressRepository: ProgressRepository
) {
    suspend operator fun invoke(userId: String, languageCode: String): Result<List<CourseProgress>> {
        return progressRepository.getCourseProgress(userId, languageCode)
    }
}
