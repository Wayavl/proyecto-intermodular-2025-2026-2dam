package com.paralearn.android.domain.use_case.language

import com.paralearn.android.domain.model.Language
import com.paralearn.android.domain.repository.LanguageRepository
import javax.inject.Inject

class GetLanguages @Inject constructor(
    private val languageRepository: LanguageRepository
) {
    suspend operator fun invoke(): Result<List<Language>> {
        return languageRepository.getLanguages()
    }
}
