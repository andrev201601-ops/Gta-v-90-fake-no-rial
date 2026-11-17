package com.example.ui.prank

import androidx.annotation.StringRes

sealed interface PrankUiState {
  data class Loading(
    val progress: Float,
    val tipIndex: Int,
    @param:StringRes val statusLabelRes: Int
  ) : PrankUiState

  data class FakeError(
    val secondsRemaining: Int,
    val isApplyingWallpaper: Boolean
  ) : PrankUiState

  data object ExitTriggered : PrankUiState
}
