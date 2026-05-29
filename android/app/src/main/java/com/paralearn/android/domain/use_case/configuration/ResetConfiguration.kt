package com.paralearn.android.domain.use_case.configuration

import com.paralearn.android.domain.repository.ConfigurationRepository
import javax.inject.Inject

class ResetConfiguration @Inject constructor(
    private val configurationRepository: ConfigurationRepository
) {
    suspend operator fun invoke(userId: String, configurationId: String): Result<Unit> {
        return configurationRepository.resetConfiguration(userId, configurationId)
    }
}
