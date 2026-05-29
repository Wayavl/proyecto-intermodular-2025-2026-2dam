package com.paralearn.android.domain.repository

import com.paralearn.android.domain.model.ConfType
import com.paralearn.android.domain.model.Configuration

interface ConfigurationRepository {
    suspend fun getUserConfigurations(userId: String, languageCode: String): Result<List<Configuration>>
    suspend fun setUserConfiguration(userId: String, configurationId: String, value: String): Result<Unit>
    suspend fun resetConfiguration(userId: String, configurationId: String): Result<Unit>
    suspend fun createConfiguration(id: String, type: ConfType, defaultValue: String): Result<Unit>
    suspend fun createConfigurationTranslation(
        configurationId: String,
        languageName: String,
        name: String,
        description: String
    ): Result<Unit>
    suspend fun deleteConfiguration(configurationId: String): Result<Unit>
}