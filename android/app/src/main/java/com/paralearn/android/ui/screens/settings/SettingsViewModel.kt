package com.paralearn.android.ui.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paralearn.android.R
import com.paralearn.android.data.locale.AppLanguage
import com.paralearn.android.data.settings.AppSettings
import com.paralearn.android.data.session.SessionManager
import com.paralearn.android.domain.model.ConfType
import com.paralearn.android.domain.model.Configuration
import com.paralearn.android.domain.use_case.configuration.GetUserConfigurations
import com.paralearn.android.domain.use_case.configuration.SetUserConfiguration
import com.paralearn.android.domain.use_case.user.GetProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingUiItem(
    val id: String,
    val title: String,
    val description: String,
    val type: ConfType,
    val value: String,
    val selectionOptions: List<String> = emptyList()
)

data class SettingsUiState(
    val settings: List<SettingUiItem> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saveMessage: String? = null,
    val localeRevision: Long = 0L
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val getProfileUseCase: GetProfile,
    private val getUserConfigurationsUseCase: GetUserConfigurations,
    private val setUserConfigurationUseCase: SetUserConfiguration,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    private var userId: String? = null

    init {
        viewModelScope.launch {
            sessionManager.username.collect { username ->
                if (username != null) loadSettings(username)
            }
        }
    }

    fun loadSettings(username: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, saveMessage = null)
            val profileResult = getProfileUseCase(username)
            val profile = profileResult.getOrNull()
            profile?.id?.let { sessionManager.setUserId(it) }
            val resolvedUserId = sessionManager.userId.value ?: profile?.id
            if (resolvedUserId.isNullOrBlank()) {
                _uiState.value = SettingsUiState(
                    isLoading = false,
                    errorMessage = profileResult.exceptionOrNull()?.message
                        ?: appContext.getString(R.string.settings_resolve_user_error)
                )
                return@launch
            }
            userId = resolvedUserId

            val lang = sessionManager.languageCode.value
            val remote = getUserConfigurationsUseCase(resolvedUserId, lang).getOrDefault(emptyList())
            val merged = mergeWithDefaults(remote)

            remote.find { it.id == AppSettings.LANGUAGE_ID }?.value?.let { storedLang ->
                if (AppLanguage.isSupported(storedLang) && storedLang != lang) {
                    sessionManager.setLanguage(storedLang)
                }
            }

            remote.find { it.id == AppSettings.THEME_ID }?.value?.let { storedTheme ->
                val darkTheme = sessionManager.parseDarkThemeValue(storedTheme)
                if (darkTheme != sessionManager.isDarkTheme.value) {
                    sessionManager.setDarkTheme(darkTheme)
                }
            }

            _uiState.value = SettingsUiState(
                settings = merged,
                isLoading = false
            )
        }
    }

    fun updateSetting(settingId: String, newValue: String) {
        val uid = userId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, saveMessage = null, errorMessage = null)

            setUserConfigurationUseCase(uid, settingId, newValue).fold(
                onSuccess = {
                    when {
                        settingId == AppSettings.LANGUAGE_ID && AppLanguage.isSupported(newValue) -> {
                            sessionManager.setLanguage(newValue)
                            loadSettings(sessionManager.username.value.orEmpty())
                            _uiState.value = _uiState.value.copy(
                                isSaving = false,
                                saveMessage = appContext.getString(R.string.saved),
                                localeRevision = System.currentTimeMillis()
                            )
                        }
                        settingId == AppSettings.THEME_ID -> {
                            sessionManager.setDarkTheme(sessionManager.parseDarkThemeValue(newValue))
                            _uiState.value = _uiState.value.copy(
                                settings = _uiState.value.settings.map { item ->
                                    if (item.id == settingId) item.copy(value = newValue) else item
                                },
                                isSaving = false,
                                saveMessage = appContext.getString(R.string.saved)
                            )
                        }
                        else -> {
                            _uiState.value = _uiState.value.copy(
                                settings = _uiState.value.settings.map { item ->
                                    if (item.id == settingId) item.copy(value = newValue) else item
                                },
                                isSaving = false,
                                saveMessage = appContext.getString(R.string.saved)
                            )
                        }
                    }
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = error.message ?: appContext.getString(R.string.settings_save_failed)
                    )
                }
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(saveMessage = null, errorMessage = null)
    }

    private fun mergeWithDefaults(remote: List<Configuration>): List<SettingUiItem> {
        val defaults = defaultCatalog()
        return defaults.map { def ->
            val match = remote.find { it.id == def.id }
            SettingUiItem(
                id = def.id,
                title = match?.displayName ?: def.title,
                description = def.description,
                type = match?.type ?: def.type,
                value = match?.value?.ifBlank { def.value } ?: def.value,
                selectionOptions = def.selectionOptions
            )
        }
    }

    private fun defaultCatalog(): List<SettingUiItem> = listOf(
        SettingUiItem(
            id = AppSettings.THEME_ID,
            title = appContext.getString(R.string.settings_theme_title),
            description = appContext.getString(R.string.settings_theme_desc),
            type = ConfType.BOOLEAN,
            value = sessionManager.isDarkTheme.value.toString()
        ),
        SettingUiItem(
            id = AppSettings.LANGUAGE_ID,
            title = appContext.getString(R.string.settings_language_title),
            description = appContext.getString(R.string.settings_language_desc),
            type = ConfType.SELECTION,
            value = sessionManager.languageCode.value,
            selectionOptions = AppLanguage.supported
        ),
        SettingUiItem(
            id = AppSettings.NOTIFICATIONS_ID,
            title = appContext.getString(R.string.settings_notifications_title),
            description = appContext.getString(R.string.settings_notifications_desc),
            type = ConfType.BOOLEAN,
            value = "true"
        ),
        SettingUiItem(
            id = AppSettings.TELEMETRY_SYNC_ID,
            title = appContext.getString(R.string.settings_telemetry_title),
            description = appContext.getString(R.string.settings_telemetry_desc),
            type = ConfType.BOOLEAN,
            value = "true"
        )
    )
}
