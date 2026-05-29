package com.paralearn.android.ui.screens.course_tree

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paralearn.android.data.session.SessionManager
import com.paralearn.android.domain.model.Course
import com.paralearn.android.domain.model.Lesson
import com.paralearn.android.domain.use_case.course.GetCourse
import com.paralearn.android.domain.use_case.lesson.GetCourseLessons
import com.paralearn.android.domain.use_case.matriculate.Enroll
import com.paralearn.android.domain.use_case.matriculate.GetEnrollments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseLessonsTreeUiState(
    val courseId: String = "",
    val course: Course? = null,
    val lessons: List<Lesson> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class CourseLessonsTreeViewModel @Inject constructor(
    private val getCourseUseCase: GetCourse,
    private val getCourseLessonsUseCase: GetCourseLessons,
    private val sessionManager: SessionManager,
    private val enrollUser: Enroll,
    private val getUserEnroll: GetEnrollments
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseLessonsTreeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.languageCode.collect { _ ->
                val activeCourseId = _uiState.value.courseId
                if (activeCourseId.isNotEmpty()) {
                    loadCourseLessonsTree(activeCourseId)
                }
            }
        }
    }

    fun loadCourseLessonsTree(courseId: String) {
        viewModelScope.launch {
            _uiState.value = CourseLessonsTreeUiState(courseId = courseId, isLoading = true)

            sessionManager.userId.value?.let {
                val userCourses = getUserEnroll(userId = it)
                userCourses.fold(
                    onSuccess = { courseList ->
                        for (i in courseList) {
                            if (i.courseId == courseId) {
                                return@let
                            }
                        }

                        enrollUser(it, courseId)
                    },
                    onFailure = { error ->

                    }
                )
            }

            val courseResult = getCourseUseCase(courseId)
            
            courseResult.fold(
                onSuccess = { courseObj ->
                    // 2. Fetch Lessons
                    getCourseLessonsUseCase(courseId).fold(
                        onSuccess = { lessonsList ->
                            val sortedLessons = lessonsList.sortedBy { it.order ?: 0 }
                            _uiState.value = CourseLessonsTreeUiState(
                                courseId = courseId,
                                course = courseObj,
                                lessons = sortedLessons,
                                isLoading = false
                            )
                        },
                        onFailure = { lessonError ->
                            // Fallback to empty lessons but keep course metadata
                            _uiState.value = CourseLessonsTreeUiState(
                                courseId = courseId,
                                course = courseObj,
                                lessons = emptyList(),
                                isLoading = false,
                                errorMessage = "Failed to load lessons: ${lessonError.message}"
                            )
                        }
                    )
                },
                onFailure = { courseError ->
                    _uiState.value = CourseLessonsTreeUiState(
                        courseId = courseId,
                        isLoading = false,
                        errorMessage = "Failed to load course details: ${courseError.message}"
                    )
                }
            )
        }
    }
}
