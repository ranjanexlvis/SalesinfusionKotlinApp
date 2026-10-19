package com.example.kmmexlvistech.presentation.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
import com.example.kmmexlvistech.presentation.ui.splash.viewModel.ApiSuccessEvent
import com.example.kmmexlvistech.presentation.ui.splash.viewModel.LoginEvent
import com.example.kmmexlvistech.presentation.ui.splash.viewModel.LoginState

@Composable
fun LoginScreen(
    loginState: LoginState,
    events: (LoginEvent) -> Unit,
    onGoogleLogin: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    navigateToMain: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }

    val isLoading = loginState.progressBarState != ProgressBarState.Idle
    val systemTokenReady = loginState.systemToken != null
    val canSubmit = !isLoading && systemTokenReady

    LaunchedEffect(Unit) {
        visible = true
        events(
            LoginEvent.SystemToken(
                "admin@salesinfusion.com",
                "bedb79ecb7ae7eaf522b4a522ff679ee0200d2ab6a0a96d2f305db7f76451996"
            )
        )
    }

    LaunchedEffect(loginState.apiSuccessEvent) {
        when (loginState.apiSuccessEvent) {
            ApiSuccessEvent.NavigateToMain -> navigateToMain()
            else -> Unit
        }
    }

    fun validate(): Boolean {
        emailError = when {
            email.isBlank() -> "Enter your email"
            !email.trim().matches(Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) -> "Enter a valid email"
            else -> null
        }
        passwordError = when {
            password.isBlank() -> "Enter your password"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
        return emailError == null && passwordError == null
    }

    fun submit() {
        if (canSubmit ) {
            events(LoginEvent.Login(email.trim(), password, loginState.systemToken))
        }
    }

    DefaultUiScreen(
        queue = loginState.errorQueue,
        onRemoveHeadFromQueue = { events(LoginEvent.OnRemoveHeadFromQueue) },
        progressBarState = loginState.progressBarState,
    ) {
        val colors = MaterialTheme.colorScheme

        val heroBrush = Brush.linearGradient(
            listOf(colors.primary, lerp(colors.primary, colors.secondary, 0.55f))
        )

        val fieldColors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colors.onSurface,
            unfocusedTextColor = colors.onSurface,
            errorTextColor = colors.onSurface,
            focusedContainerColor = colors.surfaceVariant.copy(alpha = 0.5f),
            unfocusedContainerColor = colors.surfaceVariant.copy(alpha = 0.5f),
            errorContainerColor = colors.surfaceVariant.copy(alpha = 0.5f),
            focusedBorderColor = colors.primary,
            unfocusedBorderColor = Color.Transparent,
            errorBorderColor = colors.error,
            focusedLabelColor = colors.primary,
            unfocusedLabelColor = colors.onSurfaceVariant,
            errorLabelColor = colors.error,
            focusedLeadingIconColor = colors.primary,
            unfocusedLeadingIconColor = colors.onSurfaceVariant,
            errorLeadingIconColor = colors.error,
            focusedTrailingIconColor = colors.onSurfaceVariant,
            unfocusedTrailingIconColor = colors.onSurfaceVariant,
            errorTrailingIconColor = colors.error,
            cursorColor = colors.primary,
            errorCursorColor = colors.error,
            errorSupportingTextColor = colors.error,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
                .systemBarsPadding()
                .imePadding(),
            contentAlignment = Alignment.TopCenter,
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 8 },
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 480.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // ---------- Hero + overlapping form card ----------
                    Box(modifier = Modifier.fillMaxWidth()) {

                        // Hero
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .clip(RoundedCornerShape(28.dp))
                                .background(heroBrush)
                        ) {
                            // Decorative circles
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 50.dp, y = (-50).dp)
                                    .size(190.dp)
                                    .background(colors.onPrimary.copy(alpha = 0.10f), CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .offset(x = (-40).dp, y = 60.dp)
                                    .size(150.dp)
                                    .background(colors.onPrimary.copy(alpha = 0.08f), CircleShape)
                            )

                            Column(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(start = 24.dp, top = 24.dp, end = 24.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .background(
                                            colors.onPrimary.copy(alpha = 0.18f),
                                            RoundedCornerShape(16.dp)
                                        ),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "SI",
                                        color = colors.onPrimary,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = "Welcome back",
                                    color = colors.onPrimary,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Login to continue your journey",
                                    color = colors.onPrimary.copy(alpha = 0.85f),
                                    fontSize = 14.sp,
                                )
                            }
                        }

                        // Form card (overlaps the bottom of the hero)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 196.dp, start = 12.dp, end = 12.dp),
                            shape = RoundedCornerShape(24.dp),
                            color = colors.surface,
                            contentColor = colors.onSurface,
                            shadowElevation = 8.dp,
                            border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.6f)),
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {

                                if (!systemTokenReady) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 12.dp),
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            strokeWidth = 2.dp,
                                            color = colors.primary,
                                        )
                                        Text(
                                            "Preparing secure connection…",
                                            fontSize = 12.sp,
                                            color = colors.onSurfaceVariant,
                                            modifier = Modifier.padding(start = 8.dp),
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = email,
                                    onValueChange = {
                                        email = it
                                        if (emailError != null) emailError = null
                                    },
                                    label = { Text("Email") },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true,
                                    isError = emailError != null,
                                    supportingText = emailError?.let { msg -> { Text(msg, fontSize = 12.sp) } },
                                    colors = fieldColors,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email,
                                        imeAction = ImeAction.Next,
                                    ),
                                )

                                Spacer(Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = password,
                                    onValueChange = {
                                        password = it
                                        if (passwordError != null) passwordError = null
                                    },
                                    label = { Text("Password") },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                                    trailingIcon = {
                                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                            Icon(
                                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff
                                                else Icons.Default.Visibility,
                                                contentDescription = if (passwordVisible) "Hide password"
                                                else "Show password",
                                            )
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true,
                                    isError = passwordError != null,
                                    visualTransformation = if (passwordVisible) VisualTransformation.None
                                    else PasswordVisualTransformation(),
                                    supportingText = passwordError?.let { msg -> { Text(msg, fontSize = 12.sp) } },
                                    colors = fieldColors,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Password,
                                        imeAction = ImeAction.Done,
                                    ),
                                    keyboardActions = KeyboardActions(onDone = { submit() }),
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                ) {
                                    TextButton(onClick = onForgotPasswordClick) {
                                        Text(
                                            "Forgot password?",
                                            color = colors.primary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    }
                                }

                                Spacer(Modifier.height(6.dp))

                                // Gradient primary button
                                val buttonShape = RoundedCornerShape(14.dp)
                                Button(
                                    onClick = { submit() },
                                    enabled = canSubmit,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .background(
                                            brush = if (canSubmit) heroBrush
                                            else SolidColor(colors.onSurface.copy(alpha = 0.12f)),
                                            shape = buttonShape,
                                        ),
                                    shape = buttonShape,
                                    contentPadding = PaddingValues(0.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = colors.onPrimary,
                                        disabledContainerColor = Color.Transparent,
                                        disabledContentColor = colors.onSurface.copy(alpha = 0.38f),
                                    ),
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            strokeWidth = 2.5.dp,
                                            color = colors.onSurface.copy(alpha = 0.6f),
                                        )
                                    } else {
                                        Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    // ---------- "or" divider ----------
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(colors.outlineVariant)
                        )
                        Text(
                            "  or continue with  ",
                            color = colors.onSurfaceVariant,
                            fontSize = 13.sp,
                        )
                        Box(
                            Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(colors.outlineVariant)
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    // ---------- Google ----------
                    OutlinedButton(
                        onClick = onGoogleLogin,
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, colors.outlineVariant),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = colors.surface,
                            contentColor = colors.onSurface,
                            disabledContentColor = colors.onSurface.copy(alpha = 0.38f),
                        ),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(colors.primary.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "G",
                                color = colors.primary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("Continue with Google", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}