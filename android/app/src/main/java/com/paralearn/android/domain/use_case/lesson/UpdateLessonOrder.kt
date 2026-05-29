package com.paralearn.android.domain.use_case.lesson

import com.paralearn.android.domain.repository.LessonRepository
import javax.inject.Inject

class UpdateLessonOrder @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    suspend operator fun invoke(lessonId: String, order: Int): Result<Unit> {
        return lessonRepository.updateLessonOrder(lessonId, order)
    }
}
