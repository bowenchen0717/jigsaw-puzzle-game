package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

fun Modifier.frostedBackground(): Modifier = this.drawBehind {
    // 1. Deep luxury background gradient: deep violet-charcoal to almost black plum
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF1F1235), Color(0xFF13111C)),
            center = Offset(size.width * 0.5f, size.height * 0.5f),
            radius = size.width * 1.2f
        )
    )

    // 2. High-contrast ambient blurry glows (as requested by Frosted Glass style HTML):
    // Top-right soft pink-magenta glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x3DFF7BB0), Color.Transparent),
            center = Offset(size.width * 0.95f, size.height * 0.15f),
            radius = size.width * 0.55f
        ),
        center = Offset(size.width * 0.95f, size.height * 0.15f),
        radius = size.width * 0.55f
    )

    // Mid-left soft cyan-blue glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x2E80F3FF), Color.Transparent),
            center = Offset(size.width * -0.05f, size.height * 0.55f),
            radius = size.width * 0.5f
        ),
        center = Offset(size.width * -0.05f, size.height * 0.55f),
        radius = size.width * 0.5f
    )

    // Bottom-right soft purple glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x29D0BCFF), Color.Transparent),
            center = Offset(size.width * 0.85f, size.height * 0.9f),
            radius = size.width * 0.5f
        ),
        center = Offset(size.width * 0.85f, size.height * 0.9f),
        radius = size.width * 0.5f
    )
}

private val DarkColorScheme =
  darkColorScheme(
    primary = NeonCyan,
    secondary = NeonPink,
    tertiary = NeonPurple,
    background = CyberDark,
    surface = CyberSurface,
    onPrimary = CyberDark,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
  )

private val LightColorScheme =
  darkColorScheme( // Keep dark arcade theme even in default mode for teenage appeal!
    primary = NeonCyan,
    secondary = NeonPink,
    tertiary = NeonPurple,
    background = CyberDark,
    surface = CyberSurface,
    onPrimary = CyberDark,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme for immersion and visual consistency
  dynamicColor: Boolean = false, // Disable default pastel dynamic coloring to protect the cyber identity
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
