package com.paralearn.android.ui.screens.auth

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.paralearn.android.ui.screens.auth.screens.LogInScreen
import com.paralearn.android.ui.screens.auth.screens.RegisterScreen

@Composable
fun AuthView(
    authViewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf(AuthScreen.LOGIN) }

    when (currentScreen) {
        AuthScreen.LOGIN -> {
            LogInScreen(
                viewModel = authViewModel,
                onSignUpClick = {
                    authViewModel.clearError()
                    currentScreen = AuthScreen.REGISTER
                },
                onLoginSuccess = onAuthSuccess
            )
        }
        AuthScreen.REGISTER -> {
            RegisterScreen(
                viewModel = authViewModel,
                onLoginClick = {
                    authViewModel.clearError()
                    currentScreen = AuthScreen.LOGIN
                },
                onRegisterSuccess = onAuthSuccess
            )
        }
    }
}

enum class AuthScreen {
    LOGIN, REGISTER
}