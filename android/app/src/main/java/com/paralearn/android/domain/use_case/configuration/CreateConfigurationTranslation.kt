package com.paralearn.android.domain.use_case.configuration

import com.paralearn.android.domain.repository.ConfigurationRepository
import javax.inject.Inject

class CreateConfigurationTranslation @Inject constructor(
    private val configurationRepository: ConfigurationRepository
) {
    suspend operator fun invoke(
        configurationId: String,
        languageName: String,
        name: String,
        description: String
    ): Result<Unit> {
        return configurationRepository.createConfigurationTranslation(configurationId, languageName, name, description)
    }
}
