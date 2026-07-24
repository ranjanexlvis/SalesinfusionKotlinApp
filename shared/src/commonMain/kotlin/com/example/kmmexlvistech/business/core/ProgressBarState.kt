package com.example.kmmexlvistech.business.core

sealed class ProgressBarState {
   object Idle : ProgressBarState()
   object Loading : ProgressBarState()
   object ScreenLoading : ProgressBarState()
   object FullScreenLoading : ProgressBarState()
}
