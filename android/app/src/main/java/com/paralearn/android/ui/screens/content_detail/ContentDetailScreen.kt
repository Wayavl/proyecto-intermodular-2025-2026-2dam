package com.paralearn.android.ui.screens.content_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun ContentDetailScreen(
    contentId: String,
    kind: ContentDetailKind,
    viewModel: ContentDetailViewModel,
    onBackClick: () -> Unit,
    onLaunchSandbox: (String) -> Unit = {},
    onNavigateToAlgorithm: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(contentId, kind) {
        when (kind) {
            ContentDetailKind.LESSON -> viewModel.loadLesson(contentId)
            ContentDetailKind.ALGORITHM -> viewModel.loadAlgorithm(contentId)
        }
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
                    title = if (kind == ContentDetailKind.LESSON) "LESSON" else "ALGORITHM",
                    showBackButton = true,
                    onBackClick = onBackClick
                )
            }
        ) { innerPadding ->
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryCyan)
                    }
                }
                uiState.title.isNullOrBlank() && uiState.explanationMarkdown.isBlank() -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(uiState.errorMessage ?: "Content unavailable.", color = appTextSecondaryColor())
                    }
                }
                else -> {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(scrollState)
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        ContentHeroSection(uiState)

                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            MarkdownTabPanel(
                                explanationMarkdown = uiState.explanationMarkdown,
                                useCasesMarkdown = uiState.useCasesMarkdown,
                                showUseCasesTab = uiState.showUseCasesTab,
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (uiState.kind == ContentDetailKind.LESSON && uiState.linkedAlgorithmId != null) {
                                Button(
                                    onClick = { onLaunchSandbox(uiState.linkedAlgorithmId!!) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(52.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "OPEN INTERACTIVE SANDBOX",
                                        fontFamily = SpaceGrotesk,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        ContentFooterActions(
                            uiState = uiState,
                            onPrevious = viewModel::previousLesson,
                            onNext = viewModel::nextLesson,
                            onRandomAlgorithm = {
                                viewModel.openRandomAlgorithm()?.let(onNavigateToAlgorithm)
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentHeroSection(uiState: ContentDetailUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (uiState.progressLabel != null && uiState.progressFraction != null) {
            ContentProgressBar(uiState.progressLabel, uiState.progressFraction)
        }
        uiState.subject?.let {
            Text(
                text = it.uppercase(),
                color = PrimaryCyan,
                fontSize = 11.sp,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(PrimaryCyan.copy(alpha = 0.1f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
        Text(
            text = uiState.title.orEmpty(),
            color = appTextMainColor(),
            fontSize = 32.sp,
            lineHeight = 36.sp,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun ContentProgressBar(label: String, fraction: Float) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = appTextSecondaryColor(), fontSize = 11.sp, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold)
            Text("${(fraction * 100).toInt()}% COMPLETE", color = PrimaryCyan, fontSize = 11.sp, fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(appSurfaceLowest())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(PrimaryCyan)
            )
        }
    }
}

@Composable
private fun ContentFooterActions(
    uiState: ContentDetailUiState,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onRandomAlgorithm: () -> Unit
) {
    when (uiState.kind) {
        ContentDetailKind.LESSON -> {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = onPrevious, enabled = uiState.canGoPrevious) {
                    Text(
                        "PREVIOUS LESSON",
                        color = if (uiState.canGoPrevious) PrimaryCyan else Color.Gray,
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                TextButton(onClick = onNext, enabled = uiState.canGoNext) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "NEXT LESSON",
                            color = if (uiState.canGoNext) PrimaryCyan else Color.Gray,
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = if (uiState.canGoNext) PrimaryCyan else Color.Gray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
        ContentDetailKind.ALGORITHM -> {
            Button(
                onClick = onRandomAlgorithm,
                colors = ButtonDefaults.buttonColors(containerColor = appSurfaceLowest()),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = PrimaryCyan)
                Spacer(Modifier.width(8.dp))
                Text(
                    "EXPLORE RANDOM ALGORITHM",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryCyan,
                    fontSize = 13.sp
                )
            }
        }
    }
}