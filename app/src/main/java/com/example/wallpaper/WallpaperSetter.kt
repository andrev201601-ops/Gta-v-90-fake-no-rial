package com.example.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.os.Build
import com.example.R
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface WallpaperSetter {
  suspend fun applySeiiWallpaper(): Boolean
}

class AndroidWallpaperSetter(
  private val context: Context,
  private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : WallpaperSetter {

  override suspend fun applySeiiWallpaper(): Boolean = withContext(ioDispatcher) {
    runCatching {
      val manager = WallpaperManager.getInstance(context)
      if (!manager.isWallpaperSupported) return@runCatching false
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !manager.isSetWallpaperAllowed) {
        return@runCatching false
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        try {
          manager.setResource(
            R.drawable.img_seii_wallpaper,
            WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
          )
        } catch (_: Exception) {
          manager.setResource(R.drawable.img_seii_wallpaper)
        }
      } else {
        @Suppress("DEPRECATION")
        manager.setResource(R.drawable.img_seii_wallpaper)
      }
      true
    }.getOrDefault(false)
  }
}
