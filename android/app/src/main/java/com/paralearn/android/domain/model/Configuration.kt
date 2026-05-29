package com.paralearn.android.domain.model

data class Configuration(
    val id: String,
    val value: String,
    val type: ConfType,
    val displayName: String? = null
)
