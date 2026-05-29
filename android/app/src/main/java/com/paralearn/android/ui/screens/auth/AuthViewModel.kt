package com.paralearn.android.ui.screens.auth

import com.paralearn.android.domain.classes.Email
import com.paralearn.android.domain.classes.Password
import android.content.Context
import com.paralearn.android.R
import com.paralearn.android.domain.use_case.user.LogIn
import com.paralearn.android.domain.use_case.user.Register
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val logInClass: LogIn,
    private val registerClass: Register,
) : ViewModel() {
    private val _model = MutableStateFlow(AuthModel("", Email(""), Password(""), false))
    val model = _model.asStateFlow()

    fun updateFields(newUsername: String, newEmail: String, newPassword: String, newAccept: Boolean) {
        _model.update {
            it.copy(
                username = newUsername,
                email = Email(newEmail),
                password = Password(newPassword),
                acceptTerms = newAccept,
                errorMessage = null // Clear errors when user types
            )
        }
    }

    fun clearError() {
        _model.update { it.copy(errorMessage = null) }
    }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _model.update { it.copy(isLoading = true, errorMessage = null) }
            val result = logInClass(
                password = model.value.password.value,
                actor = model.value.email.value
            )
            result.fold(
                onSuccess = {
                    _model.update { it.copy(isLoading = false, isSuccess = true) }
                    onSuccess()
                },
                onFailure = { error ->
                    _model.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: appContext.getString(R.string.auth_failed)
                        )
                    }
                }
            )
        }
    }

    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _model.update { it.copy(isLoading = true, errorMessage = null) }
            val result = registerClass(
                password = model.value.password.value,
                email = model.value.email.value,
                username = model.value.username
            )
            result.fold(
                onSuccess = {
                    _model.update { it.copy(isLoading = false, isSuccess = true) }
                    onSuccess()
                },
                onFailure = { error ->
                    _model.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: appContext.getString(R.string.auth_registration_failed)
                        )
                    }
                }
            )
        }
    }
}