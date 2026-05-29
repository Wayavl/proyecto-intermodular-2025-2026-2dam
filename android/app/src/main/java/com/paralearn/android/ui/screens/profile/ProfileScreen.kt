package com.paralearn.android.ui.screens.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.paralearn.android.R
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToCourse: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) onLoggedOut()
    }

    Box(modifier = Modifier.fillMaxSize().background(appBackgroundColor())) {
        GlowBackground()
        DotGridBackground()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                ParalearnTopBar(
                    title = stringResource(R.string.topbar_profile),
                    showBackButton = false,
                    onSettingsClick = onNavigateToSettings
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
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    ProfileHeaderCard(
                        username = uiState.user?.username ?: stringResource(R.string.explorer),
                        email = uiState.user?.email?.value.orEmpty().ifBlank {
                            stringResource(R.string.profile_no_email)
                        },
                        isPremium = uiState.user?.isPremium() == true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProfileStatChip(
                            label = stringResource(R.string.profile_streak),
                            value = stringResource(R.string.profile_streak_days, uiState.user?.streak ?: 0),
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatChip(
                            label = stringResource(R.string.profile_member_since),
                            value = uiState.memberSince,
                            modifier = Modifier.weight(1f)
                        )
                        ProfileStatChip(
                            label = stringResource(R.string.profile_completed),
                            value = uiState.completedCoursesCount.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (uiState.inProgressCourses.isNotEmpty()) {
                        ProfileSectionTitle(stringResource(R.string.profile_courses_in_progress))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            uiState.inProgressCourses.forEach { item ->
                                InProgressCourseRow(
                                    item = item,
                                    onClick = { item.course.id?.let(onNavigateToCourse) }
                                )
                            }
                        }
                    }

                    ProfileSectionTitle(stringResource(R.string.profile_account))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(appSurfaceLowest().copy(alpha = 0.85f))
                            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                    ) {
                        ProfileMenuRow(
                            icon = { Icon(Icons.Default.Settings, null, tint = PrimaryCyan) },
                            title = stringResource(R.string.profile_settings),
                            subtitle = stringResource(R.string.profile_settings_subtitle),
                            onClick = onNavigateToSettings
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                        ProfileMenuRow(
                            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = Color(0xFFFF8A80)) },
                            title = stringResource(R.string.profile_sign_out),
                            subtitle = stringResource(R.string.profile_sign_out_subtitle),
                            titleColor = Color(0xFFFF8A80),
                            onClick = { viewModel.logout(onLoggedOut) }
                        )
                    }

                    uiState.errorMessage?.let {
                        Text(it, color = Color(0xFFFF8A80), fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        color = appTextMainColor(),
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
}

@Composable
private fun ProfileHeaderCard(
    username: String,
    email: String,
    isPremium: Boolean
) {
    val initial = username.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(appSurfaceContainer().copy(alpha = 0.9f), appSurfaceLowest().copy(alpha = 0.7f))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(20.dp))
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.25f))
                .border(2.dp, PrimaryCyan.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = PrimaryCyan,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp
            )
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = username,
                    color = appTextMainColor(),
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isPremium) {
                    Text(
                        text = stringResource(R.string.pro_badge),
                        color = PrimaryCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(PrimaryCyan.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Email, contentDescription = null, tint = appTextSecondaryColor(), modifier = Modifier.size(16.dp))
                Text(
                    text = email,
                    color = appTextSecondaryColor(),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ProfileStatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(appSurfaceLowest().copy(alpha = 0.7f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, color = appTextSecondaryColor(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(
            value,
            color = appTextMainColor(),
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun InProgressCourseRow(
    item: ProfileCourseItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(appSurfaceLowest().copy(alpha = 0.6f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.course.name ?: "Course",
                color = appTextMainColor(),
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text("${item.progressPercent}%", color = PrimaryCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Text("${item.lessonCount} lessons", color = appTextSecondaryColor(), fontSize = 12.sp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(appSurfaceLow())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(item.progressPercent / 100f)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Brush.horizontalGradient(listOf(PrimaryBlue, PrimaryCyan)))
            )
        }
    }
}

@Composable
private fun ProfileMenuRow(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    titleColor: Color? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        icon()
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, color = titleColor ?: appTextMainColor(), fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(subtitle, color = appTextSecondaryColor(), fontSize = 12.sp)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = appTextSecondaryColor())
    }
}
