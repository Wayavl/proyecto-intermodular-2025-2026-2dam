package com.paralearn.android.data.dto

import com.google.gson.annotations.SerializedName

data class UpdateUsernameRequest(
    @SerializedName("new_username") val newUsername: String
)

data class UpdateEmailRequest(
    @SerializedName("new_email") val newEmail: String
)

data class UpdatePasswordRequest(
    @SerializedName("new_password") val newPassword: String
)

data class MessageResponse(
    @SerializedName("message") val message: String?
)
