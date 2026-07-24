package com.example.kmmexlvistech.presentation.ui.defaults

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.kmmexlvistech.business.core.NetworkState
import com.example.kmmexlvistech.business.core.ProgressBarState
import com.example.kmmexlvistech.business.core.Queue
import com.example.kmmexlvistech.business.core.UIComponent
import com.example.kmmexlvistech.common.ToastHandlerFactory
import com.example.kmmexlvistech.presentation.theme.ProgressDimOverlay
import com.example.kmmexlvistech.presentation.theme.SalesInfusionTheme

@Composable
fun DefaultUiScreen(
    queue: Queue<UIComponent> = Queue(mutableListOf()),
    onRemoveHeadFromQueue: () -> Unit = {},
    progressBarState: ProgressBarState = ProgressBarState.Idle,
    networkState: NetworkState = NetworkState.Good,
    onTryAgain: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    SalesInfusionTheme {
        val toastHandler = remember { ToastHandlerFactory.create() }
        val snackbarHostState = remember { SnackbarHostState() }
        val isLoading = progressBarState != ProgressBarState.Idle

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                content()

                // Drain the generic UI-event queue (toasts, dialogs, etc.)
                if (!queue.isEmpty()) {
                    queue.peek()?.let { uiComponent ->
                        LaunchedEffect(uiComponent) {
                            when (uiComponent) {
                                is UIComponent.ToastSimple -> toastHandler.showToast(uiComponent.title)
                                else -> Unit
                            }
                            onRemoveHeadFromQueue()
                        }
                    }
                }


                if (networkState == NetworkState.Failed && !isLoading) {
                    LaunchedEffect(networkState) {
                        val result = snackbarHostState.showSnackbar(
                            message = "Couldn't connect. Check your connection.",
                            actionLabel = "Retry",
                            withDismissAction = true,
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            onTryAgain()
                        }
                    }
                }


                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ProgressDimOverlay)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ) { /* swallow taps while loading */ },
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}