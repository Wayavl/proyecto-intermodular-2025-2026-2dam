package com.paralearn.android.domain.use_case.course

import com.paralearn.android.domain.model.Course
import com.paralearn.android.domain.repository.CourseRepository
import javax.inject.Inject

class GetCourses @Inject constructor(
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(): Result<List<Course>> {
        return courseRepository.getCourses()
    }
}
