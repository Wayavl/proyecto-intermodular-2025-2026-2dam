package com.paralearn.android.ui.screens.auth

import com.paralearn.android.domain.classes.Email
import com.paralearn.android.domain.classes.Password

data class AuthModel(
    val username: String,
    val email: Email,
    val password: Password,
    val acceptTerms: Boolean,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
