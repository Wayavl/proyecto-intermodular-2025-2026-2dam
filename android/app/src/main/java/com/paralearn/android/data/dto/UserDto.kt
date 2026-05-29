package com.paralearn.android.data.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("user_id") val id: String? = null,
    @SerializedName("id") val legacyId: String? = null,
    @SerializedName("username") val username: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("join_date") val joinDate: String?,
    @SerializedName("last_learn") val lastLearn: String?,
    @SerializedName("premium_expiration_date") val premiumExpirationDate: String?,
    @SerializedName("streak") val streak: Int?,
    @SerializedName("last_streak") val lastStreak: String?
)
