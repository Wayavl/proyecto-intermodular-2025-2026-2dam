package com.paralearn.android.domain.use_case.lesson

import com.paralearn.android.domain.model.Lesson
import com.paralearn.android.domain.repository.LessonRepository
import javax.inject.Inject

class CreateLesson @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    suspend operator fun invoke(courseId: String, lesson: Lesson): Result<Unit> {
        return lessonRepository.createLesson(courseId, lesson)
    }
}
