package com.paralearn.android.domain.use_case.course

import com.paralearn.android.domain.repository.CourseRepository
import javax.inject.Inject

class DeleteCourse @Inject constructor(
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(courseId: String): Result<Unit> {
        return courseRepository.deleteCourse(courseId)
    }
}
