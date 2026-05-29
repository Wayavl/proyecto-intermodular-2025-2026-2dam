package com.paralearn.android.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paralearn.android.R
import com.paralearn.android.ui.screens.auth.screens.TerminalIcon
import androidx.compose.material3.MaterialTheme
import com.paralearn.android.ui.theme.PrimaryBlue
import com.paralearn.android.ui.theme.paralearnSurfaces
import com.paralearn.android.ui.theme.PrimaryCyan
import com.paralearn.android.ui.theme.SpaceGrotesk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun NetworkImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    var bitmap by remember(url) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(url) {
        withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val bmp = BitmapFactory.decodeStream(input)
                bitmap = bmp?.asImageBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val imageBitmap = bitmap
    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier.background(Color.Gray.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = contentDescription,
                tint = Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxSize(0.4f)
            )
        }
    }
}

@Composable
fun ParalearnTopBar(
    title: String = "PARALEARN",
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null
) {
    val surfaces = paralearnSurfaces()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(surfaces.topBar)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (showBackButton && onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryCyan
                        )
                    }
                }
                Text(
                    text = title.uppercase(),
                    color = PrimaryCyan,
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    letterSpacing = 2.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (onSettingsClick != null) {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(18.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.Gray
                        )
                    }
                }
                if (onProfileClick != null) {
                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = PrimaryCyan
                        )
                    }
                }
            }
        }
        // Thin separator line with gradient fading
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            surfaces.container,
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
fun ParalearnBottomBar(
    selectedTab: String, // "home", "courses", "algorithms", "profile"
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ),
            color = MaterialTheme.colorScheme.background,
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomTabItem(
                    label = stringResource(R.string.nav_home),
                    icon = Icons.Default.Home,
                    isSelected = selectedTab == "home",
                    onClick = { onTabSelected("home") }
                )

                BottomTabItem(
                    label = stringResource(R.string.nav_courses),
                    icon = TerminalIcon,
                    isSelected = selectedTab == "courses",
                    onClick = { onTabSelected("courses") }
                )

                BottomTabItem(
                    label = stringResource(R.string.nav_algorithms),
                    icon = Icons.Default.Build,
                    isSelected = selectedTab == "algorithms",
                    onClick = { onTabSelected("algorithms") }
                )

                BottomTabItem(
                    label = stringResource(R.string.nav_profile),
                    icon = Icons.Default.Person,
                    isSelected = selectedTab == "profile",
                    onClick = { onTabSelected("profile") }
                )
            }
        }
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
}

@Composable
private fun BottomTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    if (isSelected) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryBlue)
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label.uppercase(),
                color = Color.White,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
            )
        }
    } else {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.Gray.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label.uppercase(),
                color = Color.Gray.copy(alpha = 0.7f),
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun DotGridBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val dotRadius = 1.dp.toPx()
        val spacing = 32.dp.toPx()
        val dotColor = PrimaryCyan.copy(alpha = 0.03f)

        var x = 0f
        while (x < size.width) {
            var y = 0f
            while (y < size.height) {
                drawCircle(
                    color = dotColor,
                    radius = dotRadius,
                    center = Offset(x, y)
                )
                y += spacing
            }
            x += spacing
        }
    }
}

@Composable
fun GlowBackground(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        // Top right glow
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 120.dp, y = (-120).dp)
                .size(450.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PrimaryBlue.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )
        // Bottom left glow
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-100).dp, y = 100.dp)
                .size(350.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PrimaryCyan.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}
