package com.paralearn.android.domain.use_case.configuration

import com.paralearn.android.domain.model.ConfType
import com.paralearn.android.domain.repository.ConfigurationRepository
import javax.inject.Inject

class CreateConfiguration @Inject constructor(
    private val configurationRepository: ConfigurationRepository
) {
    suspend operator fun invoke(id: String, type: ConfType, defaultValue: String): Result<Unit> {
        return configurationRepository.createConfiguration(id, type, defaultValue)
    }
}
