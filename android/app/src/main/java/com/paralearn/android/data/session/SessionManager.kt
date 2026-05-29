package com.paralearn.android.data.session

import com.paralearn.android.data.locale.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val sessionStorage: SessionStorage
) {
    private val _username = MutableStateFlow<String?>(null)
    val username = _username.asStateFlow()

    private val _userId = MutableStateFlow<String?>(null)
    val userId = _userId.asStateFlow()

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken = _authToken.asStateFlow()

    private val _languageCode = MutableStateFlow(sessionStorage.readLanguageCode())
    val languageCode = _languageCode.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(sessionStorage.readDarkTheme())
    val isDarkTheme = _isDarkTheme.asStateFlow()

    init {
        restorePersistedSession()
    }

    fun setSession(username: String) {
        _username.value = username
        persist()
    }

    fun setUserId(userId: String) {
        _userId.value = userId
        persist()
    }

    fun setAuthToken(token: String) {
        _authToken.value = token
        persist()
    }

    fun clearSession() {
        _username.value = null
        _userId.value = null
        _authToken.value = null
        sessionStorage.clear()
    }

    fun setLanguage(lang: String) {
        val normalized = AppLanguage.normalize(lang)
        _languageCode.value = normalized
        sessionStorage.saveLanguageCode(normalized)
    }

    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        sessionStorage.saveDarkTheme(enabled)
    }

    fun parseDarkThemeValue(value: String?): Boolean =
        value?.equals("false", ignoreCase = true) != true

    private fun restorePersistedSession() {
        val stored = sessionStorage.load() ?: return
        _username.value = stored.username
        _userId.value = stored.userId
        _authToken.value = stored.authToken
    }

    private fun persist() {
        val username = _username.value ?: return
        sessionStorage.save(
            username = username,
            userId = _userId.value,
            authToken = _authToken.value
        )
    }
}
