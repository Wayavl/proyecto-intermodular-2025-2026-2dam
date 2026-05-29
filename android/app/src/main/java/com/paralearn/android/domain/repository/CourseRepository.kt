package com.paralearn.android.domain.repository

import com.paralearn.android.domain.model.Course

interface CourseRepository {
    suspend fun getCourses(): Result<List<Course>>
    suspend fun getCourse(courseId: String): Result<Course>
    suspend fun createCourse(name: String, isPremium: Boolean): Result<Unit>
    suspend fun createCourseTranslation(courseId: String, languageName: String, name: String): Result<Unit>
    suspend fun updateCoursePremium(courseId: String, isPremium: Boolean): Result<Unit>
    suspend fun updateCourseTranslation(courseId: String, languageName: String, name: String): Result<Unit>
    suspend fun deleteCourse(courseId: String): Result<Unit>
}