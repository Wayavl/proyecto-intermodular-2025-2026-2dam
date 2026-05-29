package com.paralearn.android.ui.screens.content_detail

enum class ContentDetailKind {
    LESSON,
    ALGORITHM
}

enum class ContentDetailTab {
    EXPLANATION,
    USE_CASES
}

data class ControlConfig(
    val type: String,
    val id: String,
    val label: String,
    val min: Double? = null,
    val max: Double? = null,
    val step: Double? = null,
    val options: List<String>? = null,
    val defaultValue: Any? = null,
    val action: String? = null,
    val params: Map<String, Any>? = null
)

data class TelemetryState(
    val vramUsage: Float = 0.45f,
    val latencyMs: Double = 12.4,
    val throughputGflops: Double = 1450.2,
    val isSimulating: Boolean = false
)

data class ContentDetailUiState(
    val kind: ContentDetailKind = ContentDetailKind.LESSON,
    val subject: String? = null,
    val title: String? = null,
    val progressLabel: String? = null,
    val progressFraction: Float? = null,
    val explanationMarkdown: String = "",
    val useCasesMarkdown: String? = null,
    val controls: List<ControlConfig> = emptyList(),
    val controlValues: Map<String, Any> = emptyMap(),
    val telemetry: TelemetryState = TelemetryState(),
    val linkedAlgorithmId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val canGoPrevious: Boolean = false,
    val canGoNext: Boolean = false,
    val showUseCasesTab: Boolean = false,
    val showControls: Boolean = false,
    val showTelemetry: Boolean = false,
    val showGridVisualizer: Boolean = false
)
