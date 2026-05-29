package com.paralearn.android.domain.use_case.lesson

import com.paralearn.android.domain.repository.LessonRepository
import javax.inject.Inject

class UpdateLessonAlgorithm @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    suspend operator fun invoke(lessonId: String, algorithmId: String): Result<Unit> {
        return lessonRepository.updateLessonAlgorithm(lessonId, algorithmId)
    }
}
