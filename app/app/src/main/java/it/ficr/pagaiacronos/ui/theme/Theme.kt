package it.ficr.pagaiacronos.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import android.os.Build

private val Green = Color(0xFF009246)
private val Red = Color(0xFFCE2B37)
private val DarkGreen = Color(0xFF00612E)

private val LightColors = lightColorScheme(
    primary = Green,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB8F5CC),
    secondary = Red,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDAD6),
    tertiary = Color(0xFF1565C0)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6EF4A0),
    onPrimary = Color(0xFF00391D),
    primaryContainer = DarkGreen,
    secondary = Color(0xFFFFB4AA),
    onSecondary = Color(0xFF690000),
    tertiary = Color(0xFF90CAF9)
)

@Composable
fun PagaiaCronosTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(colorScheme = colorScheme, content = content)
}
