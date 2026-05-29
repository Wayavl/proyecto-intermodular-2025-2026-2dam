package com.paralearn.android.ui.screens.course_lesson

import androidx.compose.runtime.Composable
import com.paralearn.android.ui.screens.content_detail.ContentDetailKind
import com.paralearn.android.ui.screens.content_detail.ContentDetailScreen
import com.paralearn.android.ui.screens.content_detail.ContentDetailViewModel

@Composable
fun CourseLessonScreen(
    lessonId: String,
    viewModel: ContentDetailViewModel,
    onBackClick: () -> Unit,
    onLaunchSandbox: (String) -> Unit
) {
    ContentDetailScreen(
        contentId = lessonId,
        kind = ContentDetailKind.LESSON,
        viewModel = viewModel,
        onBackClick = onBackClick,
        onLaunchSandbox = onLaunchSandbox
    )
}
