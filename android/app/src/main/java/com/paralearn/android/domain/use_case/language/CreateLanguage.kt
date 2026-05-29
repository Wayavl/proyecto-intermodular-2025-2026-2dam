package com.paralearn.android.domain.use_case.language

import com.paralearn.android.domain.repository.LanguageRepository
import javax.inject.Inject

class CreateLanguage @Inject constructor(
    private val languageRepository: LanguageRepository
) {
    suspend operator fun invoke(name: String): Result<Unit> {
        return languageRepository.createLanguage(name)
    }
}
