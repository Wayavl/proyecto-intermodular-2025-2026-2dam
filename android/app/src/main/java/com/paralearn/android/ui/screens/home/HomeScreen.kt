package com.paralearn.android.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import com.paralearn.android.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paralearn.android.domain.model.Algorithm
import com.paralearn.android.ui.components.DotGridBackground
import com.paralearn.android.ui.components.GlowBackground
import com.paralearn.android.ui.components.ParalearnTopBar
import com.paralearn.android.ui.theme.PrimaryBlue
import com.paralearn.android.ui.theme.PrimaryCyan
import com.paralearn.android.ui.theme.SpaceGrotesk
import com.paralearn.android.ui.theme.appBackgroundColor
import com.paralearn.android.ui.theme.appSurfaceContainer
import com.paralearn.android.ui.theme.appSurfaceLow
import com.paralearn.android.ui.theme.appSurfaceLowest
import com.paralearn.android.ui.theme.appTextMainColor
import com.paralearn.android.ui.theme.appTextSecondaryColor

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToCourse: (String) -> Unit,
    onNavigateToAlgorithm: (String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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
                    title = stringResource(R.string.topbar_home),
                    showBackButton = false,
                    onProfileClick = onNavigateToProfile
                )
            }
        ) { innerPadding ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryCyan)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    HomeStreakCard(
                        username = uiState.user?.username ?: stringResource(R.string.explorer),
                        streakDays = uiState.user?.streak ?: 0
                    )

                    HomeSection(
                        title = stringResource(R.string.home_algorithms_learned),
                        accentColor = PrimaryCyan
                    ) {
                        if (uiState.learnedAlgorithms.isEmpty()) {
                            HomeEmptyHint(stringResource(R.string.home_algorithms_empty))
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(uiState.learnedAlgorithms, key = { it.id ?: it.title.orEmpty() }) { algo ->
                                    LearnedAlgorithmCard(
                                        algorithm = algo,
                                        onClick = { algo.id?.let(onNavigateToAlgorithm) }
                                    )
                                }
                            }
                        }
                    }

                    HomeSection(
                        title = stringResource(R.string.home_courses_in_progress),
                        accentColor = PrimaryBlue
                    ) {
                        if (uiState.inProgressCourses.isEmpty()) {
                            HomeEmptyHint(stringResource(R.string.home_courses_empty))
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                uiState.inProgressCourses.forEach { item ->
                                    InProgressCourseCard(
                                        item = item,
                                        onClick = { item.course.id?.let(onNavigateToCourse) }
                                    )
                                }
                            }
                        }
                    }

                    HomeSection(
                        title = stringResource(R.string.home_completed_courses),
                        accentColor = appTextSecondaryColor()
                    ) {
                        if (uiState.completedCourses.isEmpty()) {
                            HomeEmptyHint(stringResource(R.string.home_completed_empty))
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                uiState.completedCourses.forEach { item ->
                                    CompletedCourseRow(
                                        item = item,
                                        onClick = { item.course.id?.let(onNavigateToCourse) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeSection(
    title: String,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 22.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )
            Text(
                text = title,
                color = appTextMainColor(),
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        content()
    }
}

@Composable
private fun HomeStreakCard(username: String, streakDays: Int) {
    val milestone = 15
    val progress = (streakDays.toFloat() / milestone).coerceIn(0f, 1f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1.4f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = stringResource(R.string.home_welcome_back),
                color = PrimaryCyan,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Text(
                text = stringResource(R.string.home_hello, username),
                color = appTextMainColor(),
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 32.sp
            )
            Text(
                text = stringResource(R.string.home_streak_subtitle),
                color = appTextSecondaryColor(),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(appSurfaceContainer().copy(alpha = 0.85f))
                .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.home_streak_label),
                    color = PrimaryCyan,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Icon(Icons.Default.Star, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(16.dp))
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = streakDays.toString(),
                    color = appTextMainColor(),
                    fontSize = 30.sp,
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = stringResource(R.string.home_streak_days_suffix),
                    color = appTextSecondaryColor(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(appSurfaceLowest())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Brush.horizontalGradient(listOf(PrimaryCyan, PrimaryBlue)))
                )
            }
            Text(
                text = stringResource(R.string.home_next_milestone, milestone),
                color = appTextSecondaryColor(),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LearnedAlgorithmCard(
    algorithm: Algorithm,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(appSurfaceLowest().copy(alpha = 0.9f))
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Build, contentDescription = null, tint = PrimaryCyan, modifier = Modifier.size(18.dp))
            Text(
                text = algorithm.subject?.uppercase() ?: "KERNEL",
                color = PrimaryCyan,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = algorithm.title ?: "Algorithm",
            color = appTextMainColor(),
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.home_open_sandbox),
                color = PrimaryCyan,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = PrimaryCyan,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun InProgressCourseCard(
    item: HomeCourseItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(appSurfaceLowest().copy(alpha = 0.85f))
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = item.course.name ?: "Course",
                    color = appTextMainColor(),
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(
                        R.string.home_lessons_progress,
                        item.learnedCount,
                        item.lessonCount,
                        item.progressPercent
                    ),
                    color = appTextSecondaryColor(),
                    fontSize = 12.sp
                )
            }
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(22.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(appSurfaceLow())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(item.progressPercent / 100f)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Brush.horizontalGradient(listOf(PrimaryBlue, PrimaryCyan)))
            )
        }
    }
}

@Composable
private fun CompletedCourseRow(
    item: HomeCourseItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(appSurfaceContainer().copy(alpha = 0.45f))
            .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = PrimaryCyan.copy(alpha = 0.85f),
            modifier = Modifier.size(22.dp)
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = item.course.name ?: "Course",
                color = appTextMainColor(),
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.home_completed_lessons, item.lessonCount),
                color = appTextSecondaryColor(),
                fontSize = 11.sp
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = appTextSecondaryColor(),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun HomeEmptyHint(message: String) {
    Text(
        text = message,
        color = appTextSecondaryColor(),
        fontSize = 13.sp,
        lineHeight = 18.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(appSurfaceLowest().copy(alpha = 0.5f))
            .padding(16.dp)
    )
}
