package com.paralearn.android.data.dto

import com.google.gson.annotations.SerializedName

data class LanguageDto(
    @SerializedName("language_id") val languageId: String?,
    @SerializedName("language_name") val languageName: String?
)

data class GetLanguagesResponse(
    @SerializedName("languages") val languages: List<LanguageDto>?
)

data class CreateLanguageRequest(
    @SerializedName("name") val name: String
)
