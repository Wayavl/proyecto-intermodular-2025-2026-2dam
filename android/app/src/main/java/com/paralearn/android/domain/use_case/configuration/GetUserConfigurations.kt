package com.paralearn.android.domain.use_case.configuration

import com.paralearn.android.domain.model.Configuration
import com.paralearn.android.domain.repository.ConfigurationRepository
import javax.inject.Inject

class GetUserConfigurations @Inject constructor(
    private val configurationRepository: ConfigurationRepository
) {
    suspend operator fun invoke(userId: String, languageCode: String): Result<List<Configuration>> {
        return configurationRepository.getUserConfigurations(userId, languageCode)
    }
}
