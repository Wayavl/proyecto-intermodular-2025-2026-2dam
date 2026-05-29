package com.paralearn.android.domain.use_case.course

import com.paralearn.android.domain.model.Course
import com.paralearn.android.domain.repository.CourseRepository
import javax.inject.Inject

class GetCourse @Inject constructor(
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(courseId: String): Result<Course> {
        return courseRepository.getCourse(courseId)
    }
}
