package com.paralearn.android.ui.screens.course_tree

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paralearn.android.domain.model.Lesson
import com.paralearn.android.ui.components.DotGridBackground
import com.paralearn.android.ui.components.GlowBackground
import com.paralearn.android.ui.components.ParalearnTopBar
import com.paralearn.android.ui.theme.PrimaryBlue
import com.paralearn.android.ui.theme.PrimaryCyan
import com.paralearn.android.ui.theme.SpaceGrotesk
import com.paralearn.android.ui.theme.appBackgroundColor
import com.paralearn.android.ui.theme.appSurfaceLowest
import com.paralearn.android.ui.theme.appTextMainColor
import com.paralearn.android.ui.theme.appTextSecondaryColor

@Composable
fun CourseLessonsTreeScreen(
    courseId: String,
    viewModel: CourseLessonsTreeViewModel,
    onBackClick: () -> Unit,
    onLessonSelected: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(courseId) {
        viewModel.loadCourseLessonsTree(courseId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundColor())
    ) {
        GlowBackground()
        DotGridBackground()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                ParalearnTopBar(
                    title = uiState.course?.name?.uppercase() ?: "TRACK PATH",
                    showBackButton = true,
                    onBackClick = onBackClick,
                    onSettingsClick = null,
                    onProfileClick = null
                )
            }
        ) { innerPadding ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryCyan)
                }
            } else if (uiState.errorMessage != null && uiState.course == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage ?: "Failed to load track path",
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            } else {
                // Group lessons by their 'order' value
                val lessons = uiState.lessons
                val groupedMap = remember(lessons) {
                    lessons.groupBy { it.order ?: 0 }.toSortedMap()
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Draw a continuous glowing central track in the background
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .width(4.dp)
                            .align(Alignment.Center)
                    ) {
                        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        drawLine(
                            brush = Brush.verticalGradient(listOf(PrimaryBlue, PrimaryCyan)),
                            start = androidx.compose.ui.geometry.Offset(size.width / 2, 0f),
                            end = androidx.compose.ui.geometry.Offset(size.width / 2, size.height),
                            strokeWidth = 4f,
                            pathEffect = pathEffect
                        )
                    }

                    if (groupedMap.isEmpty()) {
                        EmptyLessonsTreePlaceholder()
                    } else {
                        val listEntries = remember(groupedMap) { groupedMap.entries.toList() }
                        
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp, start = 24.dp, end = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                TrackHeaderSection(
                                    courseName = uiState.course?.name ?: "Computational Track",
                                    lessonCount = lessons.size
                                )
                            }

                            itemsIndexed(listEntries) { rowIndex, entry ->
                                val rowLessons = entry.value
                                
                                if (rowLessons.size == 1) {
                                    // Winding single node: oscillates left, center, right based on row index
                                    val offsetMultiplier = when (rowIndex % 4) {
                                        0 -> -1f
                                        1 -> 0f
                                        2 -> 1f
                                        else -> 0f
                                    }
                                    val xOffset = (offsetMultiplier * 60).dp
                                    
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .offset(x = xOffset),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        LessonNode(
                                            lesson = rowLessons[0],
                                            nodeIndex = rowIndex + 1,
                                            onNodeClick = { onLessonSelected(rowLessons[0].id.orEmpty()) }
                                        )
                                    }
                                } else {
                                    // Multiple nodes share the same order, display side-by-side in a Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        rowLessons.forEachIndexed { nodeIndex, lesson ->
                                            if (nodeIndex > 0) {
                                                Spacer(modifier = Modifier.width(32.dp))
                                            }
                                            LessonNode(
                                                lesson = lesson,
                                                nodeIndex = rowIndex * 2 + nodeIndex + 1,
                                                onNodeClick = { onLessonSelected(lesson.id.orEmpty()) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackHeaderSection(courseName: String, lessonCount: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(PrimaryCyan.copy(alpha = 0.1f))
                .border(0.5.dp, PrimaryCyan, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = PrimaryCyan,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "ACADEMIC PATHWAY",
                    color = PrimaryCyan,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SpaceGrotesk,
                    letterSpacing = 1.sp
                )
            }
        }
        Text(
            text = courseName,
            color = appTextMainColor(),
            fontSize = 22.sp,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = "$lessonCount CORE UNITS DEFINED • ACTIVE LEARNING NODE",
            color = appTextSecondaryColor(),
            fontSize = 11.sp,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun LessonNode(
    lesson: Lesson,
    nodeIndex: Int,
    onNodeClick: () -> Unit
) {
    // Pulse animation for active lesson nodes
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Dynamic icon based on lesson order/name
    val nodeIcon = when {
        lesson.name?.contains("CUDA", ignoreCase = true) == true -> "⚡"
        lesson.name?.contains("Rust", ignoreCase = true) == true -> "⚙️"
        lesson.name?.contains("matrix", ignoreCase = true) == true -> "⇶"
        nodeIndex % 3 == 0 -> "💻"
        nodeIndex % 3 == 1 -> "⇉"
        else -> "🧠"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.width(110.dp)
    ) {
        // Glowing circular button representing the node
        Box(
            modifier = Modifier
                .size(72.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(appSurfaceLowest())
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(listOf(PrimaryBlue, PrimaryCyan)),
                    shape = CircleShape
                )
                .clickable(onClick = onNodeClick),
            contentAlignment = Alignment.Center
        ) {
            // Inner neon layer
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF040E1F)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = nodeIcon,
                        fontSize = 22.sp,
                        color = PrimaryCyan
                    )
                }
            }
        }

        // Title below the node
        Text(
            text = lesson.name ?: "Lesson $nodeIndex",
            color = appTextMainColor(),
            fontSize = 12.sp,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EmptyLessonsTreePlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(PrimaryBlue.copy(alpha = 0.1f))
                .border(1.dp, PrimaryBlue.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🛡️", fontSize = 32.sp)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Track Path Locked",
            color = appTextMainColor(),
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "No core units have been registered for this parallel processing track yet. Connect back soon.",
            color = appTextSecondaryColor(),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}
