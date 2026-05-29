package com.paralearn.android.domain.repository

import com.paralearn.android.domain.model.Lesson

interface LessonRepository {
    suspend fun getCourseLessons(courseId: String): Result<List<Lesson>>
    suspend fun getLesson(lessonId: String): Result<Lesson>
    suspend fun createLesson(courseId: String, lesson: Lesson): Result<Unit>
    suspend fun createLessonTranslation(
        lessonId: String,
        languageName: String,
        name: String,
        subject: String,
        contentMD: String
    ): Result<Unit>
    suspend fun updateLessonOrder(lessonId: String, order: Int): Result<Unit>
    suspend fun updateLessonAlgorithm(lessonId: String, algorithmId: String): Result<Unit>
    suspend fun updateLessonTranslation(
        lessonId: String,
        languageName: String,
        name: String,
        subject: String,
        contentMD: String
    ): Result<Unit>
    suspend fun deleteLesson(lessonId: String): Result<Unit>
}