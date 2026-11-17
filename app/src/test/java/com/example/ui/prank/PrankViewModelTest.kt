package com.example.ui.prank

import com.example.wallpaper.WallpaperSetter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrankViewModelTest {

  private val testDispatcher = StandardTestDispatcher()

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
  }

  @After
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `loading transitions to fake error then applies wallpaper and exits`() = runTest(testDispatcher) {
    var applyCount = 0
    val fakeSetter = object : WallpaperSetter {
      override suspend fun applySeiiWallpaper(): Boolean {
        applyCount++
        return true
      }
    }

    val viewModel = PrankViewModel(fakeSetter)
    assertTrue(viewModel.uiState.value is PrankUiState.Loading)

    // Advance past loading phases (~5.2s) to reach FakeError
    advanceTimeBy(6000L)
    runCurrent()
    assertTrue(viewModel.uiState.value is PrankUiState.FakeError)
    assertEquals(0, applyCount)

    // Advance through the 5-second auto-close countdown and wallpaper application
    advanceUntilIdle()
    assertEquals(1, applyCount)
    assertEquals(PrankUiState.ExitTriggered, viewModel.uiState.value)
  }

  @Test
  fun `clicking accept during fake error immediately applies wallpaper and exits`() = runTest(testDispatcher) {
    var applyCount = 0
    val fakeSetter = object : WallpaperSetter {
      override suspend fun applySeiiWallpaper(): Boolean {
        applyCount++
        return true
      }
    }

    val viewModel = PrankViewModel(fakeSetter)
    advanceTimeBy(6000L)
    runCurrent()
    assertTrue(viewModel.uiState.value is PrankUiState.FakeError)

    viewModel.onAcceptErrorClick()
    advanceUntilIdle()

    assertEquals(1, applyCount)
    assertEquals(PrankUiState.ExitTriggered, viewModel.uiState.value)
  }
}
