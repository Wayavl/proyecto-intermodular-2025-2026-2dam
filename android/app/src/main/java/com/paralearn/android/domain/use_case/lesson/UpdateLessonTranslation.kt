package com.paralearn.android.domain.use_case.lesson

import com.paralearn.android.domain.repository.LessonRepository
import javax.inject.Inject

class UpdateLessonTranslation @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    suspend operator fun invoke(
        lessonId: String,
        languageName: String,
        name: String,
        subject: String,
        contentMD: String
    ): Result<Unit> {
        return lessonRepository.updateLessonTranslation(lessonId, languageName, name, subject, contentMD)
    }
}
