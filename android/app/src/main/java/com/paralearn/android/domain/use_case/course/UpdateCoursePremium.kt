package com.paralearn.android.domain.use_case.course

import com.paralearn.android.domain.repository.CourseRepository
import javax.inject.Inject

class UpdateCoursePremium @Inject constructor(
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(courseId: String, isPremium: Boolean): Result<Unit> {
        return courseRepository.updateCoursePremium(courseId, isPremium)
    }
}
