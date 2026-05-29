package com.paralearn.android.domain.use_case.configuration

import com.paralearn.android.domain.repository.ConfigurationRepository
import javax.inject.Inject

class SetUserConfiguration @Inject constructor(
    private val configurationRepository: ConfigurationRepository
) {
    suspend operator fun invoke(userId: String, configurationId: String, value: String): Result<Unit> {
        return configurationRepository.setUserConfiguration(userId, configurationId, value)
    }
}
