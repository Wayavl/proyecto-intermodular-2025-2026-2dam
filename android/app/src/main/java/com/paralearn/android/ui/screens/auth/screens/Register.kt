package com.paralearn.android.ui.screens.auth.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paralearn.android.R
import com.paralearn.android.ui.screens.auth.AuthViewModel
import com.paralearn.android.ui.theme.appBackgroundColor
import com.paralearn.android.ui.theme.appSurfaceContainer
import com.paralearn.android.ui.theme.SpaceGrotesk

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onLoginClick: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val state by viewModel.model.collectAsState()
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var acceptTerms by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = appBackgroundColor()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Decorative backgrounds
            GlowBackground()
            DotGridBackground()
            SchematicLines()

            // Corner decorative lines
            CornerDecoration(modifier = Modifier.align(Alignment.TopStart))
            CornerDecoration(
                modifier = Modifier.align(Alignment.BottomEnd),
                isBottomEnd = true
            )

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Brand Anchor Header
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF2B48DB), Color(0xFF45FEC9))
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = TerminalIcon,
                            contentDescription = "Terminal Logo",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.auth_brand),
                        color = Color(0xFF45FEC9),
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 3.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.auth_register_title),
                        color = Color(0xFFD8E3FB),
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Main Glass Panel Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(appSurfaceContainer().copy(alpha = 0.7f))
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                ) {
                    // Accent Line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color.Transparent, Color(0xFF45FEC9).copy(alpha = 0.4f), Color.Transparent)
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // Full Name Field
                        Column {
                            Text(
                                text = stringResource(R.string.auth_full_name),
                                color = Color(0xFF45FEC9),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                            SchematicTextField(
                                value = nameInput,
                                onValueChange = {
                                    nameInput = it
                                    viewModel.updateFields(
                                        newUsername = it,
                                        newEmail = emailInput,
                                        newPassword = passwordInput,
                                        newAccept = acceptTerms
                                    )
                                },
                                placeholder = "Enter your full name",
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF8F909E).copy(alpha = 0.5f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Email Address Field
                        Column {
                            Text(
                                text = stringResource(R.string.auth_email),
                                color = Color(0xFF45FEC9),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                            SchematicTextField(
                                value = emailInput,
                                onValueChange = {
                                    emailInput = it
                                    viewModel.updateFields(
                                        newUsername = nameInput,
                                        newEmail = it,
                                        newPassword = passwordInput,
                                        newAccept = acceptTerms
                                    )
                                },
                                placeholder = "name@compute.com",
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = Color(0xFF8F909E).copy(alpha = 0.5f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Master Password Field
                        Column {
                            Text(
                                text = stringResource(R.string.auth_master_password),
                                color = Color(0xFF45FEC9),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                            SchematicTextField(
                                value = passwordInput,
                                onValueChange = {
                                    passwordInput = it
                                    viewModel.updateFields(
                                        newUsername = nameInput,
                                        newEmail = emailInput,
                                        newPassword = it,
                                        newAccept = acceptTerms
                                    )
                                },
                                placeholder = "••••••••••••",
                                isPassword = true,
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFF8F909E).copy(alpha = 0.5f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Terms & Conditions Checkbox
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    acceptTerms = !acceptTerms
                                    viewModel.updateFields(
                                        newUsername = nameInput,
                                        newEmail = emailInput,
                                        newPassword = passwordInput,
                                        newAccept = acceptTerms
                                    )
                                }
                        ) {
                            Checkbox(
                                checked = acceptTerms,
                                onCheckedChange = {
                                    acceptTerms = it
                                    viewModel.updateFields(
                                        newUsername = nameInput,
                                        newEmail = emailInput,
                                        newPassword = passwordInput,
                                        newAccept = it
                                    )
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF2B48DB),
                                    uncheckedColor = Color(0xFF8F909E).copy(alpha = 0.4f),
                                    checkmarkColor = Color.White
                                ),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.auth_terms),
                                color = Color(0xFFC5C5D4),
                                fontFamily = FontFamily.Default,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }

                        // Validation Error Panel
                        AnimatedVisibility(
                            visible = state.errorMessage != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF93000A).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFFFB4AB).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Error",
                                    tint = Color(0xFFFFB4AB),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = state.errorMessage ?: "Registration failed",
                                    color = Color(0xFFFFB4AB),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Register Button
                        Button(
                            onClick = {
                                viewModel.register(onRegisterSuccess)
                            },
                            enabled = !state.isLoading && nameInput.isNotBlank() && emailInput.isNotBlank() && passwordInput.isNotBlank() && acceptTerms,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2B48DB),
                                contentColor = Color.White,
                                disabledContainerColor = Color(0xFF2B48DB).copy(alpha = 0.5f),
                                disabledContentColor = Color.White.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.auth_register),
                                        fontFamily = SpaceGrotesk,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Continue",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Footer Toggle Link
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.auth_has_account),
                        color = Color(0xFFC5C5D4),
                        fontFamily = FontFamily.Default,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.auth_log_in_link),
                        color = Color(0xFF45FEC9),
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                // Technical decoration
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.auth_compute_node),
                            color = Color(0xFF8F909E).copy(alpha = 0.5f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = stringResource(R.string.auth_reg_version),
                            color = Color(0xFF45FEC9),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .height(1.dp)
                            .background(Color(0xFF8F909E).copy(alpha = 0.2f))
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF45FEC9), RoundedCornerShape(4.dp)))
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF2B48DB), RoundedCornerShape(4.dp)))
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF8F909E).copy(alpha = 0.3f), RoundedCornerShape(4.dp)))
                    }
                }
            }
        }
    }
}
