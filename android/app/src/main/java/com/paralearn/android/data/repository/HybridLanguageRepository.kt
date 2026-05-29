package com.paralearn.android.data.repository

import com.paralearn.android.data.dto.*
import com.paralearn.android.data.network.LanguageAPI
import com.paralearn.android.data.network.apiErrorMessage
import com.paralearn.android.domain.model.Language
import com.paralearn.android.domain.repository.LanguageRepository
import javax.inject.Inject

class HybridLanguageRepository @Inject constructor(
    private val languageAPI: LanguageAPI
) : LanguageRepository {

    private fun LanguageDto.toDomain(): Language {
        return Language(
            languageId = this.languageId ?: "",
            languageName = this.languageName ?: ""
        )
    }

    override suspend fun getLanguages(): Result<List<Language>> {
        return try {
            val response = languageAPI.getLanguages()
            val body = response.body()
            if (response.isSuccessful && body?.languages != null) {
                Result.success(body.languages.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.apiErrorMessage("Failed to get languages")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createLanguage(name: String): Result<Unit> {
        return try {
            val response = languageAPI.createLanguage(CreateLanguageRequest(name))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Create language failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteLanguage(name: String): Result<Unit> {
        return try {
            val response = languageAPI.deleteLanguage(name)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Delete language failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
