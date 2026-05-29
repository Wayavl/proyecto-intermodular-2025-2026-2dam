package com.paralearn.android.data.repository

import com.paralearn.android.data.dto.*
import com.paralearn.android.data.network.ConfigurationAPI
import com.paralearn.android.data.network.apiErrorMessage
import com.paralearn.android.domain.model.ConfType
import com.paralearn.android.domain.model.Configuration
import com.paralearn.android.domain.repository.ConfigurationRepository
import javax.inject.Inject

class HybridConfigurationRepository @Inject constructor(
    private val configurationAPI: ConfigurationAPI
) : ConfigurationRepository {

    private fun mapType(typeStr: String?): ConfType {
        return when (typeStr?.uppercase()) {
            "SLIDER" -> ConfType.SLIDER
            "SELECTION" -> ConfType.SELECTION
            else -> ConfType.BOOLEAN
        }
    }

    private fun ConfigurationDto.toDomain(): Configuration {
        return Configuration(
            id = this.configurationId ?: "",
            value = this.value ?: "",
            type = mapType(this.type),
            displayName = this.configurationName
        )
    }

    override suspend fun getUserConfigurations(userId: String, languageCode: String): Result<List<Configuration>> {
        return try {
            val response = configurationAPI.getUserConfigurations(userId, languageId = languageCode)
            val body = response.body()
            if (response.isSuccessful && body?.configurations != null) {
                Result.success(body.configurations.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.apiErrorMessage("Failed to get configurations")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setUserConfiguration(userId: String, configurationId: String, value: String): Result<Unit> {
        return try {
            val response = configurationAPI.setUserConfiguration(userId, SetUserConfigurationRequest(configurationId, value))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Set user configuration failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetConfiguration(userId: String, configurationId: String): Result<Unit> {
        return try {
            val response = configurationAPI.resetConfiguration(userId, configurationId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Reset user configuration failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createConfiguration(id: String, type: ConfType, defaultValue: String): Result<Unit> {
        return try {
            // Note: Hono backend endpoint takes type from request body. Default value is ignored/not supported by endpoint.
            val response = configurationAPI.createConfiguration(CreateConfigurationRequest(type.name))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Create configuration failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createConfigurationTranslation(
        configurationId: String,
        languageName: String,
        name: String,
        description: String
    ): Result<Unit> {
        return try {
            // Note: Hono backend endpoint takes language_name and configuration_name. description is ignored/not supported.
            val response = configurationAPI.createConfigurationTranslation(
                configurationId,
                CreateConfigurationTranslationRequest(languageName, name)
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Create configuration translation failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteConfiguration(configurationId: String): Result<Unit> {
        return try {
            val response = configurationAPI.deleteConfiguration(configurationId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.apiErrorMessage("Delete configuration failed")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
