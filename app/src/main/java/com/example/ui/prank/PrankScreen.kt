package com.example.ui.prank

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Gamepad
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ErrorContainerDark
import com.example.ui.theme.ErrorCrimson
import com.example.ui.theme.NeonPink
import com.example.ui.theme.SunsetOrange
import com.example.ui.theme.TerminalBg
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TwitchPurple
import com.example.ui.theme.ViceMidnight
import com.example.ui.theme.ViceSurface
import kotlin.math.roundToInt

@Composable
fun PrankScreen(
  uiState: PrankUiState,
  onAcceptErrorClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val haptic = LocalHapticFeedback.current
  val isErrorVisible = uiState is PrankUiState.FakeError || uiState is PrankUiState.ExitTriggered

  LaunchedEffect(isErrorVisible) {
    if (isErrorVisible) {
      haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }
  }

  val loadingState = uiState as? PrankUiState.Loading
  val loadingTips = stringArrayResource(id = R.array.loading_tips)

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(ViceMidnight)
  ) {
    val isCompactHeight = maxHeight < 640.dp

    // Background Vice City Artwork
    Image(
      painter = painterResource(id = R.drawable.img_gta_loading),
      contentDescription = stringResource(id = R.string.loading_bg_description),
      contentScale = ContentScale.Crop,
      modifier = Modifier.fillMaxSize()
    )

    // Atmospheric neon-twilight gradient scrim for readability
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colorStops = arrayOf(
              0.0f to ViceMidnight.copy(alpha = 0.78f),
              0.38f to ViceMidnight.copy(alpha = 0.25f),
              0.68f to ViceMidnight.copy(alpha = 0.65f),
              1.0f to ViceMidnight.copy(alpha = 0.96f)
            )
          )
        )
    )

    // Main GTA VI Loading HUD
    Column(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.statusBars)
        .windowInsetsPadding(WindowInsets.navigationBars)
        .padding(horizontal = 24.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Status Bar
      TopHudRow(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 600.dp)
      )

      // Center Hero Title Emblem
      HeroLogoBlock(
        isCompactHeight = isCompactHeight,
        modifier = Modifier
          .widthIn(max = 600.dp)
          .padding(vertical = 8.dp)
      )

      // Bottom Loading Bar & Tips Panel
      val progress = loadingState?.progress ?: 0.99f
      val tipIndex = (loadingState?.tipIndex ?: 4).coerceIn(0, loadingTips.lastIndex)
      val statusRes = loadingState?.statusLabelRes ?: R.string.loading_status_finalizing

      LoadingFooterPanel(
        progress = progress,
        tipText = loadingTips[tipIndex],
        statusText = stringResource(id = statusRes),
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 600.dp)
      )
    }

    // Dramatic red glitch tint when fake error hits
    AnimatedVisibility(
      visible = isErrorVisible,
      enter = fadeIn(tween(150)),
      exit = fadeOut(tween(150))
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.68f))
      )
    }

    // Fake Fatal Error Modal
    AnimatedVisibility(
      visible = isErrorVisible,
      enter = fadeIn(tween(180)) + scaleIn(
        initialScale = 0.88f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 420f)
      ),
      exit = fadeOut(tween(150)),
      modifier = Modifier
        .align(Alignment.Center)
        .windowInsetsPadding(WindowInsets.statusBars)
        .windowInsetsPadding(WindowInsets.navigationBars)
        .padding(horizontal = 20.dp)
    ) {
      val errorState = uiState as? PrankUiState.FakeError
      FakeErrorDialogCard(
        secondsRemaining = errorState?.secondsRemaining ?: 0,
        isApplyingWallpaper = errorState?.isApplyingWallpaper ?: true,
        onAcceptClick = onAcceptErrorClick,
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 460.dp)
      )
    }
  }
}

@Composable
private fun TopHudRow(modifier: Modifier = Modifier) {
  val infiniteTransition = rememberInfiniteTransition(label = "live_dot")
  val dotAlpha by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 0.25f,
    animationSpec = infiniteRepeatable(
      animation = tween(700, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "dot_alpha"
  )

  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Surface(
      shape = RoundedCornerShape(50),
      color = ViceSurface.copy(alpha = 0.82f),
      border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.55f))
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .alpha(dotAlpha)
            .clip(CircleShape)
            .background(NeonPink)
        )
        Text(
          text = stringResource(id = R.string.badge_online_mode),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }

    Surface(
      shape = RoundedCornerShape(8.dp),
      color = ViceSurface.copy(alpha = 0.82f),
      border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.45f))
    ) {
      Text(
        text = stringResource(id = R.string.badge_build_version),
        style = MaterialTheme.typography.labelSmall,
        color = ElectricCyan,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
      )
    }
  }
}

@Composable
private fun HeroLogoBlock(
  isCompactHeight: Boolean,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "logo_glow")
  val glowAlpha by infiniteTransition.animateFloat(
    initialValue = 0.72f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "glow_alpha"
  )

  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Icon(
        imageVector = Icons.Rounded.Gamepad,
        contentDescription = null,
        tint = ElectricCyan,
        modifier = Modifier.size(18.dp)
      )
      Text(
        text = stringResource(id = R.string.hero_title_gta),
        style = MaterialTheme.typography.displayMedium.copy(
          fontSize = if (isCompactHeight) 28.sp else 36.sp,
          letterSpacing = 6.sp
        ),
        color = Color.White
      )
    }

    Text(
      text = stringResource(id = R.string.hero_title_vi),
      style = MaterialTheme.typography.displayLarge.copy(
        fontSize = if (isCompactHeight) 88.sp else 116.sp,
        lineHeight = if (isCompactHeight) 88.sp else 112.sp,
        brush = Brush.linearGradient(
          colors = listOf(NeonPink, SunsetOrange, ElectricCyan)
        )
      ),
      modifier = Modifier.alpha(glowAlpha)
    )

    Surface(
      shape = RoundedCornerShape(6.dp),
      color = NeonPink.copy(alpha = 0.18f),
      border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.7f))
    ) {
      Text(
        text = stringResource(id = R.string.hero_subtitle),
        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 3.sp),
        color = ElectricCyan,
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
      )
    }
  }
}

@Composable
private fun LoadingFooterPanel(
  progress: Float,
  tipText: String,
  statusText: String,
  modifier: Modifier = Modifier
) {
  val animatedProgress by animateFloatAsState(
    targetValue = progress.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 120, easing = LinearEasing),
    label = "progress_anim"
  )
  val percent = (animatedProgress * 100).roundToInt()

  Card(
    modifier = modifier.testTag("loading_panel"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = ViceSurface.copy(alpha = 0.88f)
    ),
    border = BorderStroke(
      width = 1.dp,
      brush = Brush.horizontalGradient(listOf(NeonPink.copy(alpha = 0.65f), ElectricCyan.copy(alpha = 0.65f)))
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Tip Banner
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Icon(
          imageVector = Icons.Rounded.GraphicEq,
          contentDescription = null,
          tint = ElectricCyan,
          modifier = Modifier.size(20.dp)
        )
        AnimatedContent(
          targetState = tipText,
          transitionSpec = { fadeIn(tween(240)) togetherWith fadeOut(tween(180)) },
          label = "tip_transition"
        ) { targetTip ->
          Text(
            text = targetTip,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // Custom Neon Gradient Progress Bar
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(10.dp)
          .clip(RoundedCornerShape(50))
          .background(ViceMidnight)
          .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(50))
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth(animatedProgress)
            .height(10.dp)
            .clip(RoundedCornerShape(50))
            .background(
              Brush.horizontalGradient(
                colors = listOf(NeonPink, SunsetOrange, ElectricCyan)
              )
            )
        )
      }

      // Bottom Status + Spinner + Percentage
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.weight(1f)
        ) {
          CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp,
            strokeCap = StrokeCap.Round,
            color = NeonPink
          )
          Text(
            text = statusText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
          text = "${stringResource(id = R.string.loading_label_prefix)} $percent%",
          style = MaterialTheme.typography.labelSmall,
          color = ElectricCyan,
          modifier = Modifier.testTag("loading_percentage")
        )
      }
    }
  }
}

@Composable
private fun FakeErrorDialogCard(
  secondsRemaining: Int,
  isApplyingWallpaper: Boolean,
  onAcceptClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.testTag("fake_error_dialog"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = ViceSurface),
    border = BorderStroke(1.5.dp, ErrorCrimson),
    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Fake OS Window Warning Title Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.horizontalGradient(listOf(ErrorCrimson, Color(0xFFB71C4B)))
          )
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Icon(
            imageVector = Icons.Rounded.WarningAmber,
            contentDescription = stringResource(id = R.string.error_icon_description),
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
          Text(
            text = stringResource(id = R.string.error_header_code),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
          )
        }
        Icon(
          imageVector = Icons.Rounded.Close,
          contentDescription = null,
          tint = Color.White.copy(alpha = 0.85f),
          modifier = Modifier.size(18.dp)
        )
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = ErrorContainerDark,
            border = BorderStroke(1.dp, ErrorCrimson.copy(alpha = 0.6f))
          ) {
            Icon(
              imageVector = Icons.Rounded.BugReport,
              contentDescription = null,
              tint = ErrorCrimson,
              modifier = Modifier
                .padding(10.dp)
                .size(28.dp)
            )
          }

          Text(
            text = stringResource(id = R.string.error_headline),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Text(
          text = stringResource(id = R.string.error_body),
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Funny Fake Terminal Output Box
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = TerminalBg,
          border = BorderStroke(1.dp, TwitchPurple.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = stringResource(id = R.string.error_log_line_1),
              style = MaterialTheme.typography.bodyMedium,
              color = ElectricCyan
            )
            Text(
              text = stringResource(id = R.string.error_log_line_2),
              style = MaterialTheme.typography.bodyMedium,
              color = NeonPink
            )
            Text(
              text = stringResource(id = R.string.error_log_line_3),
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = TerminalGreen
            )
          }
        }

        Button(
          onClick = onAcceptClick,
          enabled = !isApplyingWallpaper,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = ErrorCrimson,
            contentColor = Color.White,
            disabledContainerColor = ErrorCrimson.copy(alpha = 0.6f),
            disabledContentColor = Color.White
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("accept_error_button")
        ) {
          if (isApplyingWallpaper) {
            CircularProgressIndicator(
              modifier = Modifier.size(18.dp),
              strokeWidth = 2.dp,
              color = Color.White
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = stringResource(id = R.string.error_button_applying),
              style = MaterialTheme.typography.labelLarge
            )
          } else {
            Text(
              text = stringResource(id = R.string.error_button_accept, secondsRemaining),
              style = MaterialTheme.typography.labelLarge
            )
          }
        }

        Text(
          text = stringResource(id = R.string.error_auto_close_hint),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}
