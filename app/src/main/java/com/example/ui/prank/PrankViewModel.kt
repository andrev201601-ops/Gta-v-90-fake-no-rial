package com.example.ui.prank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.wallpaper.WallpaperSetter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PrankViewModel(
  private val wallpaperSetter: WallpaperSetter
) : ViewModel() {

  private val _uiState = MutableStateFlow<PrankUiState>(
    PrankUiState.Loading(
      progress = 0f,
      tipIndex = 0,
      statusLabelRes = R.string.loading_status_initializing
    )
  )
  val uiState: StateFlow<PrankUiState> = _uiState.asStateFlow()

  private var sequenceJob: Job? = null
  private var countdownJob: Job? = null
  private var exitStarted = false

  init {
    startLoadingSequence()
  }

  private fun startLoadingSequence() {
    sequenceJob?.cancel()
    sequenceJob = viewModelScope.launch {
      var currentProgress = 0f
      var tipTick = 0

      // Phase 1: 0% -> 62%
      while (currentProgress < 0.62f) {
        delay(70L)
        currentProgress = (currentProgress + 0.022f).coerceAtMost(0.62f)
        tipTick++
        _uiState.value = PrankUiState.Loading(
          progress = currentProgress,
          tipIndex = (tipTick / 16) % TIPS_COUNT,
          statusLabelRes = R.string.loading_status_initializing
        )
      }

      // Phase 2: 62% -> 94%
      while (currentProgress < 0.94f) {
        delay(95L)
        currentProgress = (currentProgress + 0.018f).coerceAtMost(0.94f)
        tipTick++
        _uiState.value = PrankUiState.Loading(
          progress = currentProgress,
          tipIndex = (tipTick / 14) % TIPS_COUNT,
          statusLabelRes = R.string.loading_status_stalled
        )
      }

      // Dramatic GTA-style stall at 94%
      _uiState.value = PrankUiState.Loading(
        progress = 0.94f,
        tipIndex = 3,
        statusLabelRes = R.string.loading_status_stalled
      )
      delay(950L)

      // Phase 3: 94% -> 99%
      while (currentProgress < 0.99f) {
        delay(80L)
        currentProgress = (currentProgress + 0.01f).coerceAtMost(0.99f)
        _uiState.value = PrankUiState.Loading(
          progress = currentProgress,
          tipIndex = 4,
          statusLabelRes = R.string.loading_status_finalizing
        )
      }

      delay(300L)
      triggerFakeError()
    }
  }

  private fun triggerFakeError() {
    _uiState.value = PrankUiState.FakeError(
      secondsRemaining = AUTO_CLOSE_SECONDS,
      isApplyingWallpaper = false
    )
    countdownJob?.cancel()
    countdownJob = viewModelScope.launch {
      for (remaining in (AUTO_CLOSE_SECONDS - 1) downTo 0) {
        delay(1000L)
        if (exitStarted) return@launch
        if (remaining > 0) {
          _uiState.value = PrankUiState.FakeError(
            secondsRemaining = remaining,
            isApplyingWallpaper = false
          )
        } else {
          applyWallpaperAndExit()
        }
      }
    }
  }

  fun onAcceptErrorClick() {
    applyWallpaperAndExit()
  }

  private fun applyWallpaperAndExit() {
    if (exitStarted) return
    exitStarted = true
    countdownJob?.cancel()

    viewModelScope.launch {
      _uiState.value = PrankUiState.FakeError(
        secondsRemaining = 0,
        isApplyingWallpaper = true
      )
      wallpaperSetter.applySeiiWallpaper()
      delay(120L)
      _uiState.value = PrankUiState.ExitTriggered
    }
  }

  companion object {
    private const val TIPS_COUNT = 5
    private const val AUTO_CLOSE_SECONDS = 5

    fun factory(wallpaperSetter: WallpaperSetter): ViewModelProvider.Factory =
      object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          return PrankViewModel(wallpaperSetter) as T
        }
      }
  }
}
