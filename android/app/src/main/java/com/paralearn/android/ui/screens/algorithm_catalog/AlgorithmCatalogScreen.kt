package com.paralearn.android.ui.screens.algorithm_catalog

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paralearn.android.R
import com.paralearn.android.domain.model.Algorithm
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
fun AlgorithmCatalogScreen(
    viewModel: AlgorithmCatalogViewModel,
    onAlgorithmClick: (String) -> Unit
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
                    title = stringResource(R.string.topbar_algorithms),
                    showBackButton = false
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

                // Hero Header Text
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.algorithms_labs),
                        color = PrimaryCyan,
                        fontSize = 11.sp,
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = stringResource(R.string.algorithms_headline),
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
                            text = stringResource(R.string.algorithms_search_hint),
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

                // Main Content
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
                            text = uiState.errorMessage ?: "Failed to load kernels",
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                } else if (uiState.filteredAlgorithms.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.algorithms_no_results),
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
                        items(uiState.filteredAlgorithms) { algo ->
                            AlgorithmItemCard(
                                algorithm = algo,
                                onClick = { onAlgorithmClick(algo.id.orEmpty()) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlgorithmItemCard(
    algorithm: Algorithm,
    onClick: () -> Unit
) {
    // Generate specialized local image dynamically based on the title as a calculated view attribute
    val imageRes = when {
        algorithm.title?.contains("Matrix", ignoreCase = true) == true ->
            R.drawable.img_matrix
        algorithm.title?.contains("Sum", ignoreCase = true) == true || algorithm.title?.contains("Scan", ignoreCase = true) == true ->
            R.drawable.img_sum_scan
        else ->
            R.drawable.img_default_algo
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(appSurfaceLowest().copy(alpha = 0.8f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left artwork box
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(appSurfaceLowest())
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = algorithm.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Info column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = algorithm.subject ?: "COMPLEXITY",
                        color = PrimaryCyan,
                        fontSize = 10.sp,
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    // Premium tag
                    if (algorithm.isPremium == "true") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(PrimaryBlue.copy(alpha = 0.2f))
                                .border(0.5.dp, PrimaryBlue, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.pro_badge),
                                color = PrimaryBlue,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = algorithm.title ?: "CUDA Kernel",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = algorithm.useCase ?: "Interactive memory manipulation sandbox.",
                    color = appTextSecondaryColor(),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2
                )
            }
        }
    }
}
