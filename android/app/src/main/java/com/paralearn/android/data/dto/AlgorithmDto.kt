package com.paralearn.android.data.dto

import com.google.gson.annotations.SerializedName

data class AlgorithmDto(
    @SerializedName("algorithm_id") val algorithmId: String?,
    @SerializedName("is_premium") val isPremium: Boolean?,
    @SerializedName("controls_yml") val controlsYml: String?,
    @SerializedName("controls_json") val controlsJson: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("subject") val subject: String?,
    @SerializedName("explanation_md") val explanationMd: String?,
    @SerializedName("use_cases_md") val useCasesMd: String?
)

data class GetAlgorithmsResponse(
    @SerializedName("algorithms") val algorithms: List<AlgorithmDto>?
)

data class GetAlgorithmResponse(
    @SerializedName("algorithm") val algorithm: AlgorithmDto?
)

data class CreateAlgorithmRequest(
    @SerializedName("is_premium") val isPremium: Boolean,
    @SerializedName("controls_yml") val controlsYml: String
)

data class CreateAlgorithmResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("algorithm_id") val algorithmId: String?
)

data class CreateAlgorithmTranslationRequest(
    @SerializedName("language_name") val languageName: String,
    @SerializedName("title") val title: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("explanation_md") val explanationMd: String,
    @SerializedName("use_cases_md") val useCasesMd: String
)

data class UpdateAlgorithmPremiumRequest(
    @SerializedName("is_premium") val isPremium: Boolean
)

data class UpdateAlgorithmControlsRequest(
    @SerializedName("controls_yml") val controlsYml: String
)

data class UpdateAlgorithmTranslationRequest(
    @SerializedName("title") val title: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("explanation_md") val explanationMd: String,
    @SerializedName("use_cases_md") val useCasesMd: String
)
