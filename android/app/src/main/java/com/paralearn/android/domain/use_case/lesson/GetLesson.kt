package com.paralearn.android.domain.use_case.lesson

import com.paralearn.android.domain.model.Lesson
import com.paralearn.android.domain.repository.LessonRepository
import javax.inject.Inject

class GetLesson @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    suspend operator fun invoke(lessonId: String): Result<Lesson> {
        return lessonRepository.getLesson(lessonId)
    }
}
