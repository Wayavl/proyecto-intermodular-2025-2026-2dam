package com.paralearn.android.data.session

import android.content.Context
import android.util.Base64
import com.paralearn.android.data.locale.AppLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

data class StoredSession(
    val username: String,
    val userId: String?,
    val authToken: String?,
    val savedAtMillis: Long
)

@Singleton
class SessionStorage @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun save(username: String, userId: String?, authToken: String?) {
        prefs.edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_AUTH_TOKEN, authToken)
            .putLong(KEY_SAVED_AT, System.currentTimeMillis())
            .apply()
    }

    fun load(): StoredSession? {
        val username = prefs.getString(KEY_USERNAME, null)?.takeIf { it.isNotBlank() } ?: return null
        val savedAt = prefs.getLong(KEY_SAVED_AT, 0L)
        if (savedAt <= 0L || System.currentTimeMillis() - savedAt > SESSION_DURATION_MS) {
            clear()
            return null
        }

        val authToken = prefs.getString(KEY_AUTH_TOKEN, null)?.takeIf { it.isNotBlank() }
        if (authToken != null && !isJwtValid(authToken)) {
            clear()
            return null
        }

        return StoredSession(
            username = username,
            userId = prefs.getString(KEY_USER_ID, null)?.takeIf { it.isNotBlank() },
            authToken = authToken,
            savedAtMillis = savedAt
        )
    }

    fun saveLanguageCode(languageCode: String) {
        prefs.edit()
            .putString(KEY_LANGUAGE, AppLanguage.normalize(languageCode))
            .apply()
    }

    fun readLanguageCode(): String {
        val stored = prefs.getString(KEY_LANGUAGE, null)
        return AppLanguage.normalize(stored ?: AppLanguage.fromDeviceLocale())
    }

    fun saveDarkTheme(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
    }

    fun readDarkTheme(): Boolean = prefs.getBoolean(KEY_DARK_THEME, true)

    fun clear() {
        val language = prefs.getString(KEY_LANGUAGE, null)
        val darkTheme = prefs.getBoolean(KEY_DARK_THEME, true)
        prefs.edit().clear().apply()
        prefs.edit().putBoolean(KEY_DARK_THEME, darkTheme).apply()
        language?.let { prefs.edit().putString(KEY_LANGUAGE, it).apply() }
    }

    companion object {
        fun readLanguageCode(context: Context): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val stored = prefs.getString(KEY_LANGUAGE, null)
            return AppLanguage.normalize(stored ?: AppLanguage.fromDeviceLocale())
        }

        private const val PREFS_NAME = "paralearn_session"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_SAVED_AT = "saved_at"
        private const val KEY_LANGUAGE = "language_code"
        private const val KEY_DARK_THEME = "dark_theme"
        const val SESSION_DURATION_MS = 30L * 24 * 60 * 60 * 1000
    }

    private fun isJwtValid(token: String): Boolean {
        return try {
            val payloadSegment = token.split('.').getOrNull(1) ?: return false
            val padded = payloadSegment.padEnd(
                payloadSegment.length + (4 - payloadSegment.length % 4) % 4,
                '='
            )
            val payloadJson = String(
                Base64.decode(padded, Base64.URL_SAFE or Base64.NO_WRAP),
                Charsets.UTF_8
            )
            val exp = JSONObject(payloadJson).optLong("exp", 0L)
            exp == 0L || exp > System.currentTimeMillis() / 1000
        } catch (_: Exception) {
            true
        }
    }

}
