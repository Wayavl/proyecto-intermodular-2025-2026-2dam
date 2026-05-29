package com.paralearn.android.ui.screens.algorithm_catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paralearn.android.data.session.SessionManager
import com.paralearn.android.domain.model.Algorithm
import com.paralearn.android.domain.use_case.algorithm.GetAlgorithms
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlgorithmCatalogUiState(
    val algorithms: List<Algorithm> = emptyList(),
    val filteredAlgorithms: List<Algorithm> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AlgorithmCatalogViewModel @Inject constructor(
    private val getAlgorithmsUseCase: GetAlgorithms,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _algorithms = MutableStateFlow<List<Algorithm>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AlgorithmCatalogUiState> = combine(
        _algorithms,
        _searchQuery,
        _isLoading,
        _errorMessage
    ) { algorithms, searchQuery, isLoading, errorMessage ->
        val filtered = if (searchQuery.isBlank()) {
            algorithms
        } else {
            algorithms.filter { algo ->
                algo.title?.contains(searchQuery, ignoreCase = true) == true ||
                algo.subject?.contains(searchQuery, ignoreCase = true) == true
            }
        }
        AlgorithmCatalogUiState(
            algorithms = algorithms,
            filteredAlgorithms = filtered,
            searchQuery = searchQuery,
            isLoading = isLoading,
            errorMessage = errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AlgorithmCatalogUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            sessionManager.languageCode.collect { _ ->
                loadAlgorithms()
            }
        }
    }

    fun loadAlgorithms() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            getAlgorithmsUseCase().fold(
                onSuccess = { list ->
                    _algorithms.value = list
                    _isLoading.value = false
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "Failed to load algorithms"
                    _isLoading.value = false
                }
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
