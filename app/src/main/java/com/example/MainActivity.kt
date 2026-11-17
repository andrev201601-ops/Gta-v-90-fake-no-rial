package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.prank.PrankScreen
import com.example.ui.prank.PrankUiState
import com.example.ui.prank.PrankViewModel
import com.example.ui.theme.ViceStreamTheme
import com.example.wallpaper.AndroidWallpaperSetter

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      ViceStreamTheme {
        val viewModel: PrankViewModel = viewModel(
          factory = PrankViewModel.factory(
            AndroidWallpaperSetter(applicationContext)
          )
        )
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(uiState) {
          if (uiState is PrankUiState.ExitTriggered) {
            finishAndRemoveTask()
          }
        }

        PrankScreen(
          uiState = uiState,
          onAcceptErrorClick = viewModel::onAcceptErrorClick
        )
      }
    }
  }
}
