package com.paralearn.android.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paralearn.android.data.locale.AppLanguage
import com.paralearn.android.data.session.SessionManager
import com.paralearn.android.data.settings.AppSettings
import com.paralearn.android.domain.model.Course
import com.paralearn.android.domain.model.Lesson
import com.paralearn.android.domain.model.User
import com.paralearn.android.domain.use_case.configuration.GetUserConfigurations
import com.paralearn.android.domain.use_case.configuration.SetUserConfiguration
import com.paralearn.android.domain.use_case.course.GetCourses
import com.paralearn.android.domain.use_case.lesson.GetCourseLessons
import com.paralearn.android.domain.use_case.matriculate.GetEnrollments
import com.paralearn.android.domain.use_case.user.GetProfile
import com.paralearn.android.domain.use_case.user.LogOut
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class ProfileCourseItem(
    val course: Course,
    val progressPercent: Int,
    val lessonCount: Int
)

data class ProfileUiState(
    val user: User? = null,
    val memberSince: String = "—",
    val inProgressCourses: List<ProfileCourseItem> = emptyList(),
    val completedCoursesCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedOut: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfile,
    private val getEnrollmentsUseCase: GetEnrollments,
    private val getCoursesUseCase: GetCourses,
    private val getCourseLessonsUseCase: GetCourseLessons,
    private val getUserConfigurationsUseCase: GetUserConfigurations,
    private val setUserConfigurationUseCase: SetUserConfiguration,
    private val logOutUseCase: LogOut,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.username.collectLatest { username ->
                if (username != null) loadProfile(username)
                else _uiState.value = ProfileUiState(errorMessage = "No active session")
            }
        }
        viewModelScope.launch {
            sessionManager.languageCode.collect {
                sessionManager.username.value?.let { loadProfile(it) }
            }
        }
    }

    fun loadProfile(username: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val profileResult = getProfileUseCase(username)
            val user = profileResult.getOrNull()
            val profileError = profileResult.exceptionOrNull()?.message
            val userId = user?.id ?: sessionManager.userId.value

            if (userId != null) {
                syncLanguageFromServer(userId)
                ensureLanguagePersisted(userId)
                syncThemeFromServer(userId)
                ensureThemePersisted(userId)
            }

            val coursesById = getCoursesUseCase().getOrDefault(emptyList()).associateBy { it.id.orEmpty() }
            val enrollments = userId?.let { getEnrollmentsUseCase(it).getOrNull() }.orEmpty()
            val lessonsByCourse = loadLessonsForCourses(enrollments.map { it.courseId }.distinct())

            val inProgress = enrollments
                .filter { it.finishDate.isNullOrBlank() }
                .map { enrollment ->
                    val lessons = lessonsByCourse[enrollment.courseId].orEmpty()
                    ProfileCourseItem(
                        course = coursesById[enrollment.courseId]
                            ?: Course(
                                id = enrollment.courseId,
                                name = "Course",
                                isPremium = false,
                                description = ""
                            ),
                        progressPercent = estimateProgress(lessons),
                        lessonCount = lessons.size
                    )
                }

            val completedCount = enrollments.count { !it.finishDate.isNullOrBlank() }

            val memberSince = user?.joinDate?.let {
                DateTimeFormatter.ofPattern("MMM yyyy")
                    .withZone(ZoneId.systemDefault())
                    .format(it)
                    .uppercase()
            } ?: "—"

            _uiState.value = ProfileUiState(
                user = user,
                memberSince = memberSince,
                inProgressCourses = inProgress,
                completedCoursesCount = completedCount,
                isLoading = false,
                errorMessage = if (user == null) {
                    profileError ?: "Could not load profile"
                } else {
                    null
                }
            )
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            logOutUseCase().fold(
                onSuccess = {
                    sessionManager.clearSession()
                    _uiState.value = _uiState.value.copy(isLoggedOut = true)
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = error.message ?: "Logout failed"
                    )
                }
            )
        }
    }

    private suspend fun syncLanguageFromServer(userId: String) {
        val lang = sessionManager.languageCode.value
        getUserConfigurationsUseCase(userId, lang).getOrNull()
            ?.find { it.id == AppSettings.LANGUAGE_ID }
            ?.value
            ?.takeIf { AppLanguage.isSupported(it) }
            ?.let { sessionManager.setLanguage(it) }
    }

    private suspend fun ensureLanguagePersisted(userId: String) {
        val lang = sessionManager.languageCode.value
        val remote = getUserConfigurationsUseCase(userId, lang).getOrNull().orEmpty()
        if (remote.none { it.id == AppSettings.LANGUAGE_ID }) {
            setUserConfigurationUseCase(userId, AppSettings.LANGUAGE_ID, lang)
        }
    }

    private suspend fun syncThemeFromServer(userId: String) {
        val lang = sessionManager.languageCode.value
        getUserConfigurationsUseCase(userId, lang).getOrNull()
            ?.find { it.id == AppSettings.THEME_ID }
            ?.value
            ?.let { sessionManager.setDarkTheme(sessionManager.parseDarkThemeValue(it)) }
    }

    private suspend fun ensureThemePersisted(userId: String) {
        val lang = sessionManager.languageCode.value
        val remote = getUserConfigurationsUseCase(userId, lang).getOrNull().orEmpty()
        if (remote.none { it.id == AppSettings.THEME_ID }) {
            setUserConfigurationUseCase(userId, AppSettings.THEME_ID, sessionManager.isDarkTheme.value.toString())
        }
    }

    private suspend fun loadLessonsForCourses(courseIds: List<String>): Map<String, List<Lesson>> =
        coroutineScope {
            courseIds.map { courseId ->
                async { courseId to getCourseLessonsUseCase(courseId).getOrDefault(emptyList()) }
            }.awaitAll().toMap()
        }

    private fun estimateProgress(lessons: List<Lesson>): Int {
        if (lessons.isEmpty()) return 10
        val withAlgo = lessons.count { !it.algorithmId.isNullOrBlank() }
        return ((withAlgo.toFloat() / lessons.size) * 80f + 15f).toInt().coerceIn(12, 92)
    }
}
