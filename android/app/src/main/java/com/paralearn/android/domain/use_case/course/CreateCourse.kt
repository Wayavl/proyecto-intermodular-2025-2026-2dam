package com.paralearn.android.domain.use_case.course

import com.paralearn.android.domain.repository.CourseRepository
import javax.inject.Inject

class CreateCourse @Inject constructor(
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(name: String, isPremium: Boolean): Result<Unit> {
        return courseRepository.createCourse(name, isPremium)
    }
}
