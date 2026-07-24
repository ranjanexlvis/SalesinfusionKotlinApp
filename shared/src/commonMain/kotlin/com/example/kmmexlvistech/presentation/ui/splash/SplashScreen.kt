@file:OptIn(InternalResourceApi::class)

package com.example.kmmexlvistech.presentation.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kmmexlvistech.business.core.AppDataStore
import com.example.kmmexlvistech.common.DataStoreKeys
import com.example.kmmexlvistech.presentation.theme.LegacySplashBackground
import com.example.kmmexlvistech.presentation.theme.SplashAccent
import com.example.kmmexlvistech.presentation.theme.SplashLogoBackground
import com.example.kmmexlvistech.presentation.theme.SplashSubtitle
import com.example.kmmexlvistech.presentation.ui.splash.viewModel.LoginEvent
import com.example.kmmexlvistech.presentation.ui.splash.viewModel.LoginState
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.InternalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun SplashScreen(
    state: LoginState,
    events: (LoginEvent) -> Unit,
    navigateToMain: () -> Unit,
    navigateToLogin: () -> Unit,
    appDataStoreManager: AppDataStore
) {

    var showLogo by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showSubtitle by remember { mutableStateOf(false) }
    var showDivider by remember { mutableStateOf(false) }
    var showLoader by remember { mutableStateOf(false) }

    var token by remember { mutableStateOf("") }

    val logoScale by animateFloatAsState(
        targetValue = if (showLogo) 1f else .5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = ""
    )

    val dividerWidth by animateDpAsState(
        targetValue = if (showDivider) 72.dp else 0.dp,
        animationSpec = tween(400),
        label = ""
    )

    LaunchedEffect(Unit) {

        showLogo = true
        delay(180)

        showTitle = true
        delay(180)

        showSubtitle = true
        delay(180)

        showDivider = true
        delay(180)

        showLoader = true
    }

    LaunchedEffect(Unit) {
        token = appDataStoreManager.readValue(DataStoreKeys.TOKEN) ?: ""
    }
println("@@@token"+token)
    LaunchedEffect(token, state.navigateToMain) {

        delay(1500)

        if (token.isNotEmpty() ) {
            navigateToMain()
        } else {
            navigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LegacySplashBackground),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(76.dp)
                    .scale(logoScale)
                    .rotate(-6f)
                    .background(
                        SplashLogoBackground,
                        RoundedCornerShape(16.dp)
                    )
                    .border(
                        2.dp,
                        SplashAccent,
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "SI",
                    color = SplashAccent,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(28.dp))

            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(tween(400)) +
                        slideInVertically(tween(400)) { it / 3 }
            ) {

                Text(
                    text = "SalesInfusion",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(
                visible = showSubtitle,
                enter = fadeIn(tween(400))
            ) {

                Text(
                    text = "Infuse Power Into Your Sales",
                    color = SplashSubtitle,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(Modifier.height(20.dp))

            AnimatedVisibility(visible = showDivider) {

                Box(
                    modifier = Modifier
                        .width(dividerWidth)
                        .height(3.dp)
                        .background(
                            SplashAccent,
                            RoundedCornerShape(10.dp)
                        )
                )
            }
        }

        AnimatedVisibility(
            visible = showLoader,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        ) {

            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = SplashAccent,
                strokeWidth = 2.dp
            )
        }
    }
}