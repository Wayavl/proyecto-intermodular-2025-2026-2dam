package com.paralearn.android.domain.use_case.lesson

import com.paralearn.android.domain.repository.LessonRepository
import javax.inject.Inject

class DeleteLesson @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    suspend operator fun invoke(lessonId: String): Result<Unit> {
        return lessonRepository.deleteLesson(lessonId)
    }
}
