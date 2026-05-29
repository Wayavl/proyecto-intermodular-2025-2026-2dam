package com.paralearn.android.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paralearn.android.R
import com.paralearn.android.data.settings.AppSettings
import com.paralearn.android.domain.model.ConfType
import com.paralearn.android.ui.locale.languageLabel
import com.paralearn.android.ui.components.DotGridBackground
import com.paralearn.android.ui.components.GlowBackground
import com.paralearn.android.ui.components.ParalearnTopBar
import androidx.compose.material3.MaterialTheme
import com.paralearn.android.ui.theme.paralearnSurfaces
import com.paralearn.android.ui.theme.PrimaryBlue
import com.paralearn.android.ui.theme.PrimaryCyan
import com.paralearn.android.ui.theme.SpaceGrotesk
import com.paralearn.android.ui.theme.appSurfaceContainer
import com.paralearn.android.ui.theme.appTextMainColor
import com.paralearn.android.ui.theme.appTextSecondaryColor

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val activity = LocalContext.current as? ComponentActivity

    LaunchedEffect(uiState.localeRevision) {
        if (uiState.localeRevision > 0L) {
            activity?.recreate()
        }
    }

    LaunchedEffect(uiState.saveMessage, uiState.errorMessage) {
        if (uiState.saveMessage != null || uiState.errorMessage != null) {
            kotlinx.coroutines.delay(2500)
            viewModel.clearMessages()
        }
    }

    val surfaces = paralearnSurfaces()
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        GlowBackground()
        DotGridBackground()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                ParalearnTopBar(
                    title = stringResource(R.string.topbar_settings),
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
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.settings_intro),
                            color = appTextSecondaryColor(),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )

                        if (uiState.isSaving) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = PrimaryCyan,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.height(18.dp)
                                )
                                Text(
                                    stringResource(R.string.saving),
                                    color = PrimaryCyan,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        uiState.saveMessage?.let {
                            Text(it, color = PrimaryCyan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        uiState.errorMessage?.let {
                            Text(it, color = Color(0xFFFF8A80), fontSize = 13.sp)
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(surfaces.containerLowest.copy(alpha = 0.9f))
                                .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                        ) {
                            uiState.settings.forEachIndexed { index, item ->
                                SettingRow(
                                    item = item,
                                    onValueChange = { viewModel.updateSetting(item.id, it) }
                                )
                                if (index < uiState.settings.lastIndex) {
                                    HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    item: SettingUiItem,
    onValueChange: (String) -> Unit
) {
    val title = localizedSettingTitle(item)
    val description = localizedSettingDescription(item)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                color = appTextMainColor(),
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(text = description, color = appTextSecondaryColor(), fontSize = 12.sp, lineHeight = 16.sp)
        }

        when (item.type) {
            ConfType.SELECTION -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item.selectionOptions.forEach { option ->
                        val selected = item.value == option
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (selected) PrimaryBlue.copy(alpha = 0.35f) else appSurfaceContainer())
                                .border(
                                    1.dp,
                                    if (selected) PrimaryCyan else Color.White.copy(alpha = 0.08f),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onValueChange(option) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = languageLabel(option),
                                color = if (selected) Color.White else appTextSecondaryColor(),
                                fontFamily = SpaceGrotesk,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            ConfType.BOOLEAN -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = booleanSettingLabel(item),
                        color = appTextSecondaryColor(),
                        fontSize = 13.sp
                    )
                    Switch(
                        checked = item.value.equals("true", ignoreCase = true),
                        onCheckedChange = { onValueChange(it.toString()) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PrimaryCyan,
                            checkedTrackColor = PrimaryBlue
                        )
                    )
                }
            }
            ConfType.SLIDER -> {
                Text("Value: ${item.value}", color = appTextSecondaryColor(), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun booleanSettingLabel(item: SettingUiItem): String {
    val enabled = item.value.equals("true", ignoreCase = true)
    return when (item.id) {
        AppSettings.THEME_ID -> if (enabled) {
            stringResource(R.string.theme_dark)
        } else {
            stringResource(R.string.theme_light)
        }
        else -> if (enabled) stringResource(R.string.enabled) else stringResource(R.string.disabled)
    }
}

@Composable
private fun localizedSettingTitle(item: SettingUiItem): String = when (item.id) {
    AppSettings.THEME_ID -> stringResource(R.string.settings_theme_title)
    AppSettings.LANGUAGE_ID -> stringResource(R.string.settings_language_title)
    AppSettings.NOTIFICATIONS_ID -> stringResource(R.string.settings_notifications_title)
    AppSettings.TELEMETRY_SYNC_ID -> stringResource(R.string.settings_telemetry_title)
    else -> item.title
}

@Composable
private fun localizedSettingDescription(item: SettingUiItem): String = when (item.id) {
    AppSettings.THEME_ID -> stringResource(R.string.settings_theme_desc)
    AppSettings.LANGUAGE_ID -> stringResource(R.string.settings_language_desc)
    AppSettings.NOTIFICATIONS_ID -> stringResource(R.string.settings_notifications_desc)
    AppSettings.TELEMETRY_SYNC_ID -> stringResource(R.string.settings_telemetry_desc)
    else -> item.description
}
