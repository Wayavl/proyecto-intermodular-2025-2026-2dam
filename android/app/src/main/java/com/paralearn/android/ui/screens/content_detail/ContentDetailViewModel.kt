package com.paralearn.android.ui.screens.content_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paralearn.android.data.session.SessionManager
import com.paralearn.android.domain.model.Algorithm
import com.paralearn.android.domain.model.Lesson
import com.paralearn.android.domain.use_case.algorithm.ExecuteAlgorithm
import com.paralearn.android.domain.use_case.algorithm.GetAlgorithm
import com.paralearn.android.domain.use_case.algorithm.GetAlgorithms
import com.paralearn.android.domain.use_case.lesson.GetCourseLessons
import com.paralearn.android.domain.use_case.lesson.GetLesson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContentDetailViewModel @Inject constructor(
    private val getLessonUseCase: GetLesson,
    private val getCourseLessonsUseCase: GetCourseLessons,
    private val getAlgorithmUseCase: GetAlgorithm,
    private val getAlgorithmsUseCase: GetAlgorithms,
    private val executeAlgorithmUseCase: ExecuteAlgorithm,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContentDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val gson = Gson()
    private var lessons: List<Lesson> = emptyList()
    private var currentLessonIndex: Int = 0
    private var algorithmPool: List<String> = emptyList()
    private var currentAlgorithmId: String? = null

    init {
        viewModelScope.launch {
            var skipFirst = true
            sessionManager.languageCode.collect {
                if (skipFirst) {
                    skipFirst = false
                    return@collect
                }
                reloadForLanguageChange()
            }
        }
    }

    fun loadLesson(lessonId: String) {
        viewModelScope.launch {
            _uiState.value = ContentDetailUiState(kind = ContentDetailKind.LESSON, isLoading = true)
            getLessonUseCase(lessonId).fold(
                onSuccess = { lesson -> applyLesson(lesson, lessonId) },
                onFailure = { applyMockLesson(lessonId) }
            )
        }
    }

    fun loadAlgorithm(algorithmId: String) {
        viewModelScope.launch {
            currentAlgorithmId = algorithmId
            _uiState.value = ContentDetailUiState(kind = ContentDetailKind.ALGORITHM, isLoading = true)
            loadAlgorithmPool()
            getAlgorithmUseCase(algorithmId).fold(
                onSuccess = { algo -> applyAlgorithm(algo) },
                onFailure = { applyMockAlgorithm() }
            )
        }
    }

    fun nextLesson() {
        if (currentLessonIndex < lessons.size - 1) {
            lessons[currentLessonIndex + 1].id?.let { loadLesson(it) }
        }
    }

    fun previousLesson() {
        if (currentLessonIndex > 0) {
            lessons[currentLessonIndex - 1].id?.let { loadLesson(it) }
        }
    }

    fun openRandomAlgorithm(): String? {
        val pool = algorithmPool.filter { it != currentAlgorithmId }
        val pickFrom = pool.ifEmpty { algorithmPool }
        val nextId = pickFrom.randomOrNull() ?: return null
        loadAlgorithm(nextId)
        return nextId
    }

    fun updateControlValue(controlId: String, newValue: Any) {
        val map = _uiState.value.controlValues.toMutableMap()
        map[controlId] = newValue
        _uiState.value = _uiState.value.copy(controlValues = map)
    }

    fun executeDynamicAlgorithm(buttonId: String, buttonParams: Map<String, Any>) {
        val algoId = currentAlgorithmId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                telemetry = _uiState.value.telemetry.copy(isSimulating = true)
            )

            // Gather dynamic values
            val dynamicValues = _uiState.value.controlValues.toMutableMap()
            // Merge with buttonParams
            dynamicValues.putAll(buttonParams)
            // Add button metadata
            dynamicValues["button_id"] = buttonId

            // Convert to JSON string
            val paramsJson = gson.toJson(dynamicValues)

            val startTime = System.currentTimeMillis()
            executeAlgorithmUseCase(algoId, paramsJson).fold(
                onSuccess = { result ->
                    val duration = System.currentTimeMillis() - startTime
                    val telemetryData = try {
                        val resultMap: Map<String, Any> = gson.fromJson(
                            result,
                            object : TypeToken<Map<String, Any>>() {}.type
                        )
                        val latency = (resultMap["latency_ms"] as? Double) ?: duration.toDouble()
                        val gflops = (resultMap["gflops"] as? Double) ?: 100.0
                        val vramMb = (resultMap["vram_mb"] as? Double) ?: 0.0

                        TelemetryState(
                            vramUsage = (vramMb / 12.0).coerceIn(0.0, 1.0).toFloat(),
                            latencyMs = latency,
                            throughputGflops = gflops,
                            isSimulating = false
                        )
                    } catch (e: Exception) {
                        TelemetryState(
                            vramUsage = 0.5f,
                            latencyMs = duration.toDouble(),
                            throughputGflops = 250.0,
                            isSimulating = false
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        telemetry = telemetryData,
                        errorMessage = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        telemetry = _uiState.value.telemetry.copy(isSimulating = false),
                        errorMessage = error.localizedMessage ?: "Execution failed"
                    )
                }
            )
        }
    }

    private fun reloadForLanguageChange() {
        when (_uiState.value.kind) {
            ContentDetailKind.LESSON -> lessons.getOrNull(currentLessonIndex)?.id?.let { loadLesson(it) }
            ContentDetailKind.ALGORITHM -> currentAlgorithmId?.let { loadAlgorithm(it) }
        }
    }

    private suspend fun loadAlgorithmPool() {
        getAlgorithmsUseCase().fold(
            onSuccess = { list ->
                algorithmPool = list.mapNotNull { it.id }.ifEmpty { fallbackAlgorithmIds() }
            },
            onFailure = { algorithmPool = fallbackAlgorithmIds() }
        )
    }

    private fun applyLesson(lesson: Lesson, requestedId: String) {
        val courseId = lesson.courseId.orEmpty()
        if (courseId.isNotEmpty()) {
            viewModelScope.launch {
                getCourseLessonsUseCase(courseId).fold(
                    onSuccess = { list ->
                        lessons = list.sortedBy { it.order ?: 0 }
                        currentLessonIndex = lessons.indexOfFirst { it.id == requestedId }.coerceAtLeast(0)
                        emitLessonState(lesson)
                    },
                    onFailure = {
                        lessons = listOf(lesson)
                        currentLessonIndex = 0
                        emitLessonState(lesson)
                    }
                )
            }
        } else {
            lessons = listOf(lesson)
            currentLessonIndex = 0
            emitLessonState(lesson)
        }
    }

    private fun emitLessonState(lesson: Lesson) {
        val total = lessons.size.coerceAtLeast(1)
        val index = currentLessonIndex.coerceAtLeast(0)
        _uiState.value = ContentDetailUiState(
            kind = ContentDetailKind.LESSON,
            subject = lesson.subject,
            title = lesson.name,
            progressLabel = "LESSON ${index + 1} OF $total",
            progressFraction = (index + 1).toFloat() / total,
            explanationMarkdown = lesson.contentMD.orEmpty(),
            useCasesMarkdown = null,
            linkedAlgorithmId = lesson.algorithmId,
            isLoading = false,
            canGoPrevious = index > 0,
            canGoNext = index < lessons.size - 1,
            showUseCasesTab = false,
            showControls = false,
            showTelemetry = false,
            showGridVisualizer = false
        )
    }

    private fun applyAlgorithm(algo: Algorithm) {
        currentAlgorithmId = algo.id
        val controls = parseControls(algo.controls)
        val defaults = controls.associate { ctrl ->
            ctrl.id to (ctrl.defaultValue ?: defaultFor(ctrl))
        }.filterValues { it != null }.mapValues { it.value!! }

        _uiState.value = ContentDetailUiState(
            kind = ContentDetailKind.ALGORITHM,
            subject = algo.subject,
            title = algo.title,
            explanationMarkdown = algo.explanation.orEmpty(),
            useCasesMarkdown = algo.useCase,
            controls = controls,
            controlValues = defaults,
            isLoading = false,
            showUseCasesTab = !algo.useCase.isNullOrBlank(),
            showControls = controls.isNotEmpty(),
            showTelemetry = true,
            showGridVisualizer = true
        )
    }

    private fun applyMockLesson(lessonId: String) {
        val lesson = Lesson(
            id = lessonId,
            courseId = "mock-course",
            algorithmId = "mock-algo-matrix",
            name = "Matrix Multiplication on GPU",
            subject = "Unit 04: Linear Algebra Acceleration",
            contentMD = """
                GPU-based matrix multiplication (GEMM) leverages massive parallelism to perform dot products simultaneously. By tiling matrices and using **Shared Memory**, we minimize expensive global memory fetches.

                ### Memory Coalescing
                Efficiency is achieved when consecutive threads access consecutive memory addresses, allowing the hardware to combine multiple requests into a single transaction.

                ```cuda
                __global__ void gemm(float* C, const float* A, const float* B, int N) {
                    int row = blockIdx.y * blockDim.y + threadIdx.y;
                    int col = blockIdx.x * blockDim.x + threadIdx.x;
                    if (row < N && col < N) {
                        float sum = 0.0f;
                        for (int k = 0; k < N; ++k) {
                            sum += A[row * N + k] * B[k * N + col];
                        }
                        C[row * N + col] = sum;
                    }
                }
                ```
            """.trimIndent(),
            order = 4,
            algorithm = null
        )
        lessons = listOf(lesson)
        currentLessonIndex = 0
        emitLessonState(lesson)
    }

    private fun applyMockAlgorithm() {
        val mockControlsJson = """
            [
              {"type":"slider","id":"matrix_dim","label":"MATRIX DIMENSIONS","min":256.0,"max":4096.0,"step":256.0,"defaultValue":1024.0},
              {"type":"select","id":"block_size","label":"BLOCK SIZE (THREADS)","options":["8","16","32","64"],"defaultValue":"32"},
              {"type":"toggle","id":"shared_tiling","label":"Shared Memory Tiling","defaultValue":true},
              {"type":"toggle","id":"coalescing","label":"Memory Coalescing","defaultValue":true}
            ]
        """.trimIndent()
        applyAlgorithm(
            Algorithm(
                id = currentAlgorithmId ?: "mock-algo-matrix",
                title = "Matrix Multiplication on GPU",
                subject = "Unit 04: Linear Algebra Acceleration",
                useCase = "Used in AI training, graphics pipelines, and scientific compute where dense linear algebra dominates runtime.",
                explanation = """
                    Matrix multiplication is split into thread blocks mapping to a 2D grid. By utilizing **Shared Memory Tiling**, submatrices are loaded into on-chip cache, reducing global memory latency.

                    ### Warp Scheduling
                    Warps hide latency by switching execution while waiting on memory. Tuning block size changes occupancy and register pressure.
                """.trimIndent(),
                controls = mockControlsJson,
                isPremium = "false"
            )
        )
    }

    private fun parseControls(raw: String?): List<ControlConfig> {
        if (raw.isNullOrBlank()) return emptyList()
        return try {
            val type = object : TypeToken<List<ControlConfig>>() {}.type
            gson.fromJson<List<ControlConfig>>(raw, type)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun defaultFor(ctrl: ControlConfig): Any? = when (ctrl.type) {
        "slider" -> ctrl.min ?: 0.0
        "select" -> ctrl.options?.firstOrNull()
        "toggle" -> false
        else -> null
    }

    private fun fallbackAlgorithmIds(): List<String> = listOf(
        "mock-algo-matrix",
        "mock-algo-scan",
        "mock-algo-atomic"
    )
}
