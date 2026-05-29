package com.paralearn.android.domain.use_case.course

import com.paralearn.android.domain.repository.CourseRepository
import javax.inject.Inject

class UpdateCourseTranslation @Inject constructor(
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(courseId: String, languageName: String, name: String): Result<Unit> {
        return courseRepository.updateCourseTranslation(courseId, languageName, name)
    }
}
