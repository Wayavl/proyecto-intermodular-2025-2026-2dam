package com.paralearn.android.ui.screens.auth.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paralearn.android.R
import com.paralearn.android.ui.screens.auth.AuthViewModel
import com.paralearn.android.ui.theme.appBackgroundColor
import com.paralearn.android.ui.theme.appSurfaceContainer
import com.paralearn.android.ui.theme.SpaceGrotesk

@Composable
fun LogInScreen(
    viewModel: AuthViewModel,
    onSignUpClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.model.collectAsState()
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = appBackgroundColor()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Decorative background elements
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
                HeaderSection()

                Spacer(modifier = Modifier.height(32.dp))

                // Glass Panel Card
                CardSection(
                    emailInput = emailInput,
                    onEmailChange = {
                        emailInput = it
                        viewModel.updateFields(
                            newUsername = state.username,
                            newEmail = it,
                            newPassword = passwordInput,
                            newAccept = state.acceptTerms
                        )
                    },
                    passwordInput = passwordInput,
                    onPasswordChange = {
                        passwordInput = it
                        viewModel.updateFields(
                            newUsername = state.username,
                            newEmail = emailInput,
                            newPassword = it,
                            newAccept = state.acceptTerms
                        )
                    },
                    isLoading = state.isLoading,
                    errorMessage = state.errorMessage,
                    onLoginClick = {
                        viewModel.login(onLoginSuccess)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Footer / Toggle Link
                FooterSection(onSignUpClick = onSignUpClick)
            }
        }
    }
}

// Background custom drawings
@Composable
fun DotGridBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val dotRadius = 1.dp.toPx()
        val spacing = 40.dp.toPx()
        val dotColor = Color(0xFF45FEC9).copy(alpha = 0.05f)

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
                .offset(x = 100.dp, y = (-100).dp)
                .size(500.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF2B48DB).copy(alpha = 0.12f),
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
                .size(400.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF45FEC9).copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

@Composable
fun SchematicLines(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val lineColor = Color(0xFF454652).copy(alpha = 0.15f)
        // Top horizontal line
        drawLine(
            color = lineColor,
            start = Offset(0f, 160.dp.toPx()),
            end = Offset(size.width, 160.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )
        // Bottom horizontal line
        drawLine(
            color = lineColor,
            start = Offset(0f, size.height - 200.dp.toPx()),
            end = Offset(size.width, size.height - 200.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )
        // Left vertical line
        drawLine(
            color = lineColor,
            start = Offset(size.width * 0.25f, 0f),
            end = Offset(size.width * 0.25f, size.height),
            strokeWidth = 1.dp.toPx()
        )
    }
}

@Composable
fun CornerDecoration(
    modifier: Modifier = Modifier,
    isBottomEnd: Boolean = false
) {
    Box(
        modifier = modifier
            .padding(32.dp)
            .size(64.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val color = if (isBottomEnd) Color(0xFF2B48DB) else Color(0xFF45FEC9)
            val stroke = 2.dp.toPx()
            if (!isBottomEnd) {
                // Top-Left corner L-shape
                drawLine(color, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = stroke)
                drawLine(color, Offset(0f, 0f), Offset(0f, size.height), strokeWidth = stroke)
            } else {
                // Bottom-Right corner L-shape
                drawLine(color, Offset(0f, size.height), Offset(size.width, size.height), strokeWidth = stroke)
                drawLine(color, Offset(size.width, 0f), Offset(size.width, size.height), strokeWidth = stroke)
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Custom Terminal Icon Box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF2B48DB), RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = TerminalIcon,
                    contentDescription = "Terminal",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.auth_brand),
                color = Color(0xFF45FEC9),
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Black,
                fontSize = 28.sp,
                letterSpacing = 2.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.auth_login_title),
            color = Color(0xFFD8E3FB),
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.auth_login_subtitle),
            color = Color(0xFFC5C5D4),
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
fun CardSection(
    emailInput: String,
    onEmailChange: (String) -> Unit,
    passwordInput: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onLoginClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(appSurfaceContainer().copy(alpha = 0.7f))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
    ) {
        // Top Gradient Accent Line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2B48DB),
                            Color(0xFF45FEC9),
                            Color(0xFF2B48DB)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Email Field
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.auth_user_identifier),
                        color = Color(0xFFD8E3FB),
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = stringResource(R.string.auth_validate_pending),
                        color = Color(0xFF45FEC9).copy(alpha = 0.8f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp
                    )
                }
                SchematicTextField(
                    value = emailInput,
                    onValueChange = onEmailChange,
                    placeholder = "user@compute.node"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Password Field
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.auth_password),
                        color = Color(0xFFD8E3FB),
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = stringResource(R.string.auth_forgot_password),
                        color = Color(0xFF45FEC9),
                        fontFamily = SpaceGrotesk,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        modifier = Modifier.clickable { /* Forgot Password Flow */ }
                    )
                }
                SchematicTextField(
                    value = passwordInput,
                    onValueChange = onPasswordChange,
                    placeholder = "••••••••••••",
                    isPassword = true
                )
            }

            // Validation Error Panel
            AnimatedVisibility(
                visible = errorMessage != null,
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
                        text = errorMessage ?: "Credentials mismatch",
                        color = Color(0xFFFFB4AB),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Button
            Button(
                onClick = onLoginClick,
                enabled = !isLoading && emailInput.isNotBlank() && passwordInput.isNotBlank(),
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
                if (isLoading) {
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
                            text = stringResource(R.string.auth_log_in),
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Black,
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

            Spacer(modifier = Modifier.height(20.dp))

            // Tech Footer Decoration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(6.dp).background(Color(0xFF45FEC9), RoundedCornerShape(3.dp)))
                    Box(modifier = Modifier.size(6.dp).background(Color(0xFF454652).copy(alpha = 0.3f), RoundedCornerShape(3.dp)))
                    Box(modifier = Modifier.size(6.dp).background(Color(0xFF454652).copy(alpha = 0.3f), RoundedCornerShape(3.dp)))
                }
                Text(
                    text = stringResource(R.string.auth_system_status),
                    color = Color(0xFF8F909E).copy(alpha = 0.4f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FooterSection(onSignUpClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.auth_no_account),
                color = Color(0xFFC5C5D4),
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.auth_sign_up),
                color = Color(0xFF45FEC9),
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable { onSignUpClick() }
            )
        }
        Spacer(modifier = Modifier.height(36.dp))

    }
}

@Composable
fun SchematicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { isFocused = it.isFocused },
        textStyle = TextStyle(
            color = Color(0xFFD8E3FB),
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        cursorBrush = SolidColor(Color(0xFF45FEC9)),
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        decorationBox = { innerTextField ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color(0xFF8F909E).copy(alpha = 0.4f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp
                        )
                    }
                    innerTextField()
                    if (trailingIcon != null) {
                        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                            trailingIcon()
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            if (isFocused) Color(0xFF2B48DB) else Color(0xFF454652).copy(alpha = 0.3f)
                        )
                )
            }
        }
    )
}

// Custom Vector Icons for high-fidelity look
val TerminalIcon: ImageVector
    get() = ImageVector.Builder(
        name = "TerminalIcon",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).path(
        fill = SolidColor(Color.White)
    ) {
        moveTo(20f, 4f)
        lineTo(4f, 4f)
        curveTo(2.9f, 4f, 2f, 4.9f, 2f, 6f)
        verticalLineTo(18f)
        curveTo(2f, 19.1f, 2.9f, 20f, 4f, 20f)
        lineTo(20f, 20f)
        curveTo(21.1f, 20f, 22f, 19.1f, 22f, 18f)
        verticalLineTo(6f)
        curveTo(22f, 4.9f, 21.1f, 4f, 20f, 4f)
        close()
        moveTo(20f, 18f)
        lineTo(4f, 18f)
        verticalLineTo(8f)
        lineTo(20f, 8f)
        verticalLineTo(18f)
        close()
        moveTo(18f, 15f)
        lineTo(12f, 15f)
        verticalLineTo(17f)
        lineTo(18f, 17f)
        verticalLineTo(15f)
        close()
        moveTo(6.5f, 10.5f)
        lineTo(10f, 13f)
        lineTo(6.5f, 15.5f)
        lineTo(8f, 16.5f)
        lineTo(12.5f, 13f)
        lineTo(8f, 9.5f)
        lineTo(6.5f, 10.5f)
        close()
    }.build()
