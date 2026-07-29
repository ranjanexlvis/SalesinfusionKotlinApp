package com.example.kmmexlvistech.presentation.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.presentation.theme.DashboardBackground
import com.example.kmmexlvistech.presentation.theme.LoginCardBorder
import com.example.kmmexlvistech.presentation.ui.defaults.DefaultUiScreen
import com.example.kmmexlvistech.presentation.ui.splash.viewModel.ApiSuccessEvent
import com.example.kmmexlvistech.presentation.ui.splash.viewModel.LoginEvent
import com.example.kmmexlvistech.presentation.ui.splash.viewModel.LoginState


@Composable
fun LoginScreen(
    loginState: LoginState,
    events: (LoginEvent) -> Unit,
    onGoogleLogin: () -> Unit = {},
    navigateToMain: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading = loginState.progressBarState != ProgressBarState.Idle
    val systemTokenReady = loginState.systemToken != null


    LaunchedEffect(Unit) {
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
            null -> Unit
            else -> Unit
        }
    }

    fun validate(): Boolean {

        passwordError = when {
            password.isBlank() -> "Enter your password"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
        return emailError == null && passwordError == null
    }

    DefaultUiScreen(
        queue = loginState.errorQueue,
        onRemoveHeadFromQueue = { events(LoginEvent.OnRemoveHeadFromQueue) },
        progressBarState = loginState.progressBarState,
    ) {
        val colors = MaterialTheme.colorScheme

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DashboardBackground),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(56.dp))

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(colors.primary.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "SI",
                        color = colors.primary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = "Login to continue your journey",
                    fontSize = 14.sp,
                    color = colors.onBackground.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 28.dp),
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface, RoundedCornerShape(16.dp))
                        .border(1.dp, LoginCardBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
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
                                color = colors.onSurface.copy(alpha = 0.6f),
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
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = emailError != null,
                        supportingText = {
                            emailError?.let { Text(it, color = colors.error, fontSize = 12.sp) }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.outline,
                            cursorColor = colors.primary,
                            focusedLabelColor = colors.primary,
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    )

                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (passwordError != null) passwordError = null
                        },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        isError = passwordError != null,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = colors.onSurface.copy(alpha = 0.6f),
                                )
                            }
                        },
                        supportingText = {
                            passwordError?.let { Text(it, color = colors.error, fontSize = 12.sp) }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.outline,
                            cursorColor = colors.primary,
                            focusedLabelColor = colors.primary,
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    )

                    Spacer(Modifier.height(22.dp))

                    Button(
                        onClick = {
                            if (validate()) {
                                events(LoginEvent.Login(email.trim(), password, loginState.systemToken))
                            }
                        },
                        enabled = !isLoading && systemTokenReady,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.primary,
                            contentColor = colors.onPrimary,
                            disabledContainerColor = colors.primary.copy(alpha = 0.4f),
                            disabledContentColor = colors.onPrimary.copy(alpha = 0.7f),
                        ),
                    ) {
                        Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    "Or continue with",
                    color = colors.onBackground.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                )

                Spacer(Modifier.height(14.dp))

                OutlinedButton(
                    onClick = onGoogleLogin,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colors.outline),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.onBackground),
                ) {
                    Text("Continue with Google", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}