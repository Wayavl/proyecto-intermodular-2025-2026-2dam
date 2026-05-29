package com.paralearn.android.ui.screens.algorithm_lesson

import androidx.compose.runtime.Composable
import com.paralearn.android.ui.screens.content_detail.ContentDetailKind
import com.paralearn.android.ui.screens.content_detail.ContentDetailScreen
import com.paralearn.android.ui.screens.content_detail.ContentDetailViewModel

@Composable
fun AlgorithmSandboxScreen(
    algorithmId: String,
    viewModel: ContentDetailViewModel,
    onBackClick: () -> Unit,
    onNavigateToAlgorithm: (String) -> Unit
) {
    ContentDetailScreen(
        contentId = algorithmId,
        kind = ContentDetailKind.ALGORITHM,
        viewModel = viewModel,
        onBackClick = onBackClick,
        onNavigateToAlgorithm = onNavigateToAlgorithm
    )
}
