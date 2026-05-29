package com.paralearn.android

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.paralearn.android.data.locale.LocaleHelper
import com.paralearn.android.data.session.SessionManager
import com.paralearn.android.ui.navigation.AppNavHost
import com.paralearn.android.ui.screens.auth.AuthView
import com.paralearn.android.ui.theme.ParalearnTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var sessionManager: SessionManager

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by sessionManager.isDarkTheme.collectAsState()
            ParalearnTheme(darkTheme = isDarkTheme) {
                val isLoggedIn = sessionManager.username.collectAsState().value != null

                if (isLoggedIn) {
                    val appNavController = rememberNavController()
                    AppNavHost(navController = appNavController)
                } else {
                    AuthView()
                }
            }
        }
    }
}
