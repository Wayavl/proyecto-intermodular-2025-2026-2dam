package com.paralearn.android.domain.use_case.configuration

import com.paralearn.android.domain.repository.ConfigurationRepository
import javax.inject.Inject

class DeleteConfiguration @Inject constructor(
    private val configurationRepository: ConfigurationRepository
) {
    suspend operator fun invoke(configurationId: String): Result<Unit> {
        return configurationRepository.deleteConfiguration(configurationId)
    }
}
