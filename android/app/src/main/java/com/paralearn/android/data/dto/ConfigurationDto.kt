package com.paralearn.android.data.dto

import com.google.gson.annotations.SerializedName

data class ConfigurationDto(
    @SerializedName("configuration_id") val configurationId: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("value") val value: String?,
    @SerializedName("configuration_name") val configurationName: String?
)

data class GetUserConfigurationsResponse(
    @SerializedName("configurations") val configurations: List<ConfigurationDto>?
)

data class SetUserConfigurationRequest(
    @SerializedName("configuration_id") val configurationId: String,
    @SerializedName("value") val value: String
)

data class CreateConfigurationRequest(
    @SerializedName("type") val type: String
)

data class CreateConfigurationResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("configuration_id") val configurationId: String?
)

data class CreateConfigurationTranslationRequest(
    @SerializedName("language_name") val languageName: String,
    @SerializedName("configuration_name") val configurationName: String
)
