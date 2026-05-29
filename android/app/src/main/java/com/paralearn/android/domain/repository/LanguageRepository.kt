package com.paralearn.android.domain.repository

import com.paralearn.android.domain.model.Language

interface LanguageRepository {
    suspend fun getLanguages(): Result<List<Language>>
    suspend fun createLanguage(name: String): Result<Unit>
    suspend fun deleteLanguage(name: String): Result<Unit>
}
