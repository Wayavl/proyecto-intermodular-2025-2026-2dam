package com.paralearn.android.domain.use_case.language

import com.paralearn.android.domain.repository.LanguageRepository
import javax.inject.Inject

class DeleteLanguage @Inject constructor(
    private val languageRepository: LanguageRepository
) {
    suspend operator fun invoke(name: String): Result<Unit> {
        return languageRepository.deleteLanguage(name)
    }
}
