package com.paralearn.android.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paralearn.android.data.session.SessionManager
import com.paralearn.android.domain.model.Algorithm
import com.paralearn.android.domain.model.Course
import com.paralearn.android.domain.model.User
import com.paralearn.android.domain.use_case.course.GetCourses
import com.paralearn.android.domain.use_case.matriculate.GetEnrollments
import com.paralearn.android.domain.use_case.progress.GetLearnedAlgorithms
import com.paralearn.android.domain.use_case.user.GetProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeCourseItem(
    val course: Course,
    val progressPercent: Int,
    val lessonCount: Int,
    val learnedCount: Int
)

data class HomeUiState(
    val user: User? = null,
    val learnedAlgorithms: List<Algorithm> = emptyList(),
    val inProgressCourses: List<HomeCourseItem> = emptyList(),
    val completedCourses: List<HomeCourseItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProfileUseCase: GetProfile,
    private val getLearnedAlgorithmsUseCase: GetLearnedAlgorithms,
    private val getEnrollmentsUseCase: GetEnrollments, // Usamos el mismo del Profile
    private val getCoursesUseCase: GetCourses,         // Para traer la información e idioma de los cursos
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                sessionManager.username,
                sessionManager.languageCode
            ) { username, languageCode ->
                Pair(username, languageCode)
            }.collectLatest { (username, languageCode) ->
                if (username != null && languageCode.isNotBlank()) {
                    loadDashboardData(username, languageCode)
                } else if (username == null) {
                    _uiState.value = HomeUiState(errorMessage = "No active session")
                }
            }
        }
    }

    fun loadDashboardData(username: String, lang: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val profileResult = getProfileUseCase(username)
            val user = profileResult.getOrNull()

            val userId = user?.id ?: sessionManager.userId.value

            if (userId.isNullOrBlank()) {
                _uiState.value = _uiState.value.copy(
                    user = user,
                    isLoading = false,
                    errorMessage = profileResult.exceptionOrNull()?.message ?: "User id unavailable"
                )
                return@launch
            }

            user?.id?.let { sessionManager.setUserId(it) }

            // Clave: Traemos las matrículas puras (las mismas que lee el perfil de forma correcta)
            val learnedAlgorithmsResult = getLearnedAlgorithmsUseCase(userId, lang)
            val enrollmentsResult = getEnrollmentsUseCase(userId)
            val coursesResult = getCoursesUseCase() // Trae los cursos con su traducción

            val learnedAlgorithms = learnedAlgorithmsResult.getOrDefault(emptyList())
            val enrollments = enrollmentsResult.getOrDefault(emptyList())

            // Indexamos los cursos por ID para cruzarlos de forma eficiente en O(1)
            val coursesById =
                coursesResult.getOrDefault(emptyList()).associateBy { it.id.orEmpty() }

            // Procesamos cursos en progreso (finishDate nulo o en blanco)
            val inProgress = enrollments
                .filter { it.finishDate.isNullOrBlank() }
                .mapNotNull { enrollment ->
                    val courseData = coursesById[enrollment.courseId] ?: Course(
                        id = enrollment.courseId,
                        name = "Curso de DOP",
                        isPremium = false,
                        description = ""
                    )
                    HomeCourseItem(
                        course = courseData,
                        progressPercent = 0, // Se inicializa en cero al no tener lecciones completadas aún
                        lessonCount = 0,
                        learnedCount = 0
                    )
                }

            // Procesamos cursos completados (finishDate tiene contenido)
            val completed = enrollments
                .filter { !it.finishDate.isNullOrBlank() }
                .mapNotNull { enrollment ->
                    val courseData = coursesById[enrollment.courseId] ?: return@mapNotNull null
                    HomeCourseItem(
                        course = courseData,
                        progressPercent = 100,
                        lessonCount = 10,
                        learnedCount = 10
                    )
                }

            val errorMsg = listOfNotNull(
                profileResult.exceptionOrNull()?.message,
                learnedAlgorithmsResult.exceptionOrNull()?.message,
                enrollmentsResult.exceptionOrNull()?.message,
                coursesResult.exceptionOrNull()?.message
            ).firstOrNull()

            _uiState.value = HomeUiState(
                user = user,
                learnedAlgorithms = learnedAlgorithms,
                inProgressCourses = inProgress,
                completedCourses = completed,
                isLoading = false,
                errorMessage = errorMsg
            )
        }
    }
}