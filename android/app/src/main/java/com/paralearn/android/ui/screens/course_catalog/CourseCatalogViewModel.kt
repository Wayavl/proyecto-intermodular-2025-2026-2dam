package com.paralearn.android.ui.screens.course_catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paralearn.android.data.session.SessionManager
import com.paralearn.android.domain.model.Course
import com.paralearn.android.domain.use_case.course.GetCourses
import com.paralearn.android.domain.use_case.matriculate.Enroll
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseCatalogUiState(
    val courses: List<Course> = emptyList(),
    val filteredCourses: List<Course> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val enrollingCourseId: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class CourseCatalogViewModel @Inject constructor(
    private val getCoursesUseCase: GetCourses,
    private val enrollUseCase: Enroll,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _enrollingCourseId = MutableStateFlow<String?>(null)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CourseCatalogUiState> = combine(
        _courses,
        _searchQuery,
        _isLoading,
        _enrollingCourseId,
        _errorMessage
    ) { courses, searchQuery, isLoading, enrollingCourseId, errorMessage ->
        val filtered = if (searchQuery.isBlank()) {
            courses
        } else {
            courses.filter { course ->
                course.name?.contains(searchQuery, ignoreCase = true) == true
            }
        }
        CourseCatalogUiState(
            courses = courses,
            filteredCourses = filtered,
            searchQuery = searchQuery,
            isLoading = isLoading,
            enrollingCourseId = enrollingCourseId,
            errorMessage = errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CourseCatalogUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            sessionManager.languageCode.collect { _ ->
                loadCourses()
            }
        }
    }

    fun loadCourses() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            getCoursesUseCase().fold(
                onSuccess = { list ->
                    _courses.value = list
                    _isLoading.value = false
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Failed to retrieve courses"
                    _isLoading.value = false
                }
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun enrollAndNavigate(courseId: String, onNavigate: (String) -> Unit) {
        viewModelScope.launch {
            _enrollingCourseId.value = courseId
            val userId = sessionManager.username.value ?: "user@example.com"
            enrollUseCase(userId, courseId).fold(
                onSuccess = {
                    _enrollingCourseId.value = null
                    onNavigate(courseId)
                },
                onFailure = {
                    // Navigate anyway even if they are already enrolled (so the flow is smooth)
                    _enrollingCourseId.value = null
                    onNavigate(courseId)
                }
            )
        }
    }
}
