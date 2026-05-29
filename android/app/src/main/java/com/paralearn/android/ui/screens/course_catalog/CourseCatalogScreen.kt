package com.paralearn.android.ui.screens.course_catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paralearn.android.R
import com.paralearn.android.domain.model.Course
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
fun CourseCatalogScreen(
    viewModel: CourseCatalogViewModel,
    onCourseClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

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
                    title = stringResource(R.string.topbar_catalog),
                    showBackButton = false,
                    onSettingsClick = null,
                    onProfileClick = null
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Hero Header text
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.catalog_tracks),
                        color = PrimaryCyan,
                        fontSize = 11.sp,
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = stringResource(R.string.catalog_headline),
                        color = appTextMainColor(),
                        fontSize = 20.sp,
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Search Bar
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.catalog_search_hint),
                            color = appTextSecondaryColor().copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = PrimaryCyan
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    tint = appTextSecondaryColor()
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(appSurfaceLowest().copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = appTextMainColor(),
                        unfocusedTextColor = appTextMainColor(),
                        focusedBorderColor = PrimaryCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                        cursorColor = PrimaryCyan
                    ),
                    singleLine = true
                )

                // Content Block
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryCyan)
                    }
                } else if (uiState.errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "An error occurred",
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                } else if (uiState.filteredCourses.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.catalog_no_results),
                            color = appTextSecondaryColor(),
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(uiState.filteredCourses) { course ->
                            val isEnrolling = uiState.enrollingCourseId == course.id
                            val isAnyEnrolling = uiState.enrollingCourseId != null
                            CourseItemCard(
                                course = course,
                                isEnrolling = isEnrolling,
                                isClickable = !isAnyEnrolling,
                                onClick = {
                                    viewModel.enrollAndNavigate(course.id.orEmpty(), onCourseClick)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CourseItemCard(
    course: Course,
    isEnrolling: Boolean = false,
    isClickable: Boolean = true,
    onClick: () -> Unit
) {
    // Generate specialized subtitle/details dynamically based on ID or index
    val subject = ""
    val description = course.description ?: ""

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(appSurfaceLowest().copy(alpha = 0.8f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .clickable(enabled = isClickable, onClick = onClick)
            .padding(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = subject,
                    color = PrimaryCyan,
                    fontSize = 10.sp,
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                if (course.isPremium == true) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(PrimaryBlue.copy(alpha = 0.2f))
                            .border(0.5.dp, PrimaryBlue, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.Yellow,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = stringResource(R.string.pro_badge),
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SpaceGrotesk
                            )
                        }
                    }
                }
            }

            Text(
                text = course.name ?: "Unnamed Course",
                color = appTextMainColor(),
                fontSize = 18.sp,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = description,
                color = appTextSecondaryColor(),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(PrimaryCyan.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isEnrolling) {
                        CircularProgressIndicator(
                            color = PrimaryCyan,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = if (isEnrolling) "ENROLLING CONCURRENTLY..." else "BEGIN TRACK",
                    color = PrimaryCyan,
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
