package com.paralearn.android.domain.repository

import com.paralearn.android.domain.model.Algorithm
import com.paralearn.android.domain.model.CourseProgress

interface ProgressRepository {
    suspend fun getLearnedAlgorithms(userId: String, languageCode: String): Result<List<Algorithm>>
    suspend fun getCourseProgress(userId: String, languageCode: String): Result<List<CourseProgress>>
}
