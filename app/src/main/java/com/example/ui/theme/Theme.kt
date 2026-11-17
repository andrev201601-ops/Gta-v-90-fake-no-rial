package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ViceDarkColorScheme = darkColorScheme(
  primary = NeonPink,
  onPrimary = TextPrimary,
  secondary = ElectricCyan,
  onSecondary = ViceMidnight,
  tertiary = SunsetOrange,
  onTertiary = ViceMidnight,
  background = ViceMidnight,
  onBackground = TextPrimary,
  surface = ViceSurface,
  onSurface = TextPrimary,
  surfaceVariant = ViceSurfaceElevated,
  onSurfaceVariant = TextSecondary,
  error = ErrorCrimson,
  onError = TextPrimary,
  errorContainer = ErrorContainerDark,
  onErrorContainer = TextPrimary,
  outline = OutlineNeon
)

@Composable
fun ViceStreamTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = ViceDarkColorScheme,
    typography = AppTypography,
    content = content
  )
}
