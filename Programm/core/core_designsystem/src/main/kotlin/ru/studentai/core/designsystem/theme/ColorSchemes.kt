package ru.studentai.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Бренд-палитра StudentAI.
 *
 * Палитра отражает академическую семантику (тёмно-синий primary, бирюзовый secondary,
 * тёплый янтарный tertiary) и одновременно сохраняет современный M3-look.
 *
 * Все значения соответствуют Material 3 Token Naming. При ребрендинге достаточно
 * заменить значения в этом файле — компоненты темы автоматически подхватят новые цвета.
 */

// ─────────── Brand seeds ───────────────────────────────────────────────────
private val Brand_Primary40 = Color(0xFF1A4FA0)
private val Brand_Primary80 = Color(0xFFAFC6FF)
private val Brand_Primary90 = Color(0xFFD9E2FF)
private val Brand_Primary10 = Color(0xFF001A41)
private val Brand_Primary20 = Color(0xFF002E69)
private val Brand_Primary30 = Color(0xFF004494)

private val Brand_Secondary40 = Color(0xFF3D8BB0)
private val Brand_Secondary80 = Color(0xFFA7CDDE)
private val Brand_Secondary90 = Color(0xFFC5E7F7)
private val Brand_Secondary10 = Color(0xFF001E2C)
private val Brand_Secondary20 = Color(0xFF003547)
private val Brand_Secondary30 = Color(0xFF1F4D63)

private val Brand_Tertiary40 = Color(0xFFC77F4E)
private val Brand_Tertiary80 = Color(0xFFFFB785)
private val Brand_Tertiary90 = Color(0xFFFFDCC2)
private val Brand_Tertiary10 = Color(0xFF311300)
private val Brand_Tertiary20 = Color(0xFF512400)
private val Brand_Tertiary30 = Color(0xFF733A14)

private val Neutral0 = Color(0xFF000000)
private val Neutral10 = Color(0xFF1B1B1F)
private val Neutral20 = Color(0xFF303034)
private val Neutral90 = Color(0xFFE3E2E6)
private val Neutral95 = Color(0xFFF1F0F4)
private val Neutral99 = Color(0xFFFEFBFF)
private val Neutral100 = Color(0xFFFFFFFF)

private val Error40 = Color(0xFFBA1A1A)
private val Error80 = Color(0xFFFFB4AB)
private val Error90 = Color(0xFFFFDAD6)
private val Error10 = Color(0xFF410002)
private val Error20 = Color(0xFF690005)
private val Error30 = Color(0xFF93000A)

public val LightColorScheme: ColorScheme = lightColorScheme(
    primary = Brand_Primary40,
    onPrimary = Color.White,
    primaryContainer = Brand_Primary90,
    onPrimaryContainer = Brand_Primary10,
    inversePrimary = Brand_Primary80,

    secondary = Brand_Secondary40,
    onSecondary = Color.White,
    secondaryContainer = Brand_Secondary90,
    onSecondaryContainer = Brand_Secondary10,

    tertiary = Brand_Tertiary40,
    onTertiary = Color.White,
    tertiaryContainer = Brand_Tertiary90,
    onTertiaryContainer = Brand_Tertiary10,

    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = Color(0xFFE1E2EC),
    onSurfaceVariant = Color(0xFF44464F),
    surfaceTint = Brand_Primary40,
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF1F0F4),

    error = Error40,
    onError = Color.White,
    errorContainer = Error90,
    onErrorContainer = Error10,

    outline = Color(0xFF74777F),
    outlineVariant = Color(0xFFC4C6D0),
    scrim = Color.Black,

    surfaceBright = Color(0xFFFEFBFF),
    surfaceDim = Color(0xFFDED9DD),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF7F2F7),
    surfaceContainer = Color(0xFFF1ECF1),
    surfaceContainerHigh = Color(0xFFEBE6EB),
    surfaceContainerHighest = Color(0xFFE6E0E5),
)

public val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = Brand_Primary80,
    onPrimary = Brand_Primary20,
    primaryContainer = Brand_Primary30,
    onPrimaryContainer = Brand_Primary90,
    inversePrimary = Brand_Primary40,

    secondary = Brand_Secondary80,
    onSecondary = Brand_Secondary20,
    secondaryContainer = Brand_Secondary30,
    onSecondaryContainer = Brand_Secondary90,

    tertiary = Brand_Tertiary80,
    onTertiary = Brand_Tertiary20,
    tertiaryContainer = Brand_Tertiary30,
    onTertiaryContainer = Brand_Tertiary90,

    background = Neutral10,
    onBackground = Neutral90,
    surface = Neutral10,
    onSurface = Neutral90,
    surfaceVariant = Color(0xFF44464F),
    onSurfaceVariant = Color(0xFFC4C6D0),
    surfaceTint = Brand_Primary80,
    inverseSurface = Neutral90,
    inverseOnSurface = Neutral20,

    error = Error80,
    onError = Error20,
    errorContainer = Error30,
    onErrorContainer = Error90,

    outline = Color(0xFF8E9099),
    outlineVariant = Color(0xFF44464F),
    scrim = Color.Black,

    surfaceBright = Color(0xFF3A383C),
    surfaceDim = Neutral10,
    surfaceContainerLowest = Color(0xFF0E0E11),
    surfaceContainerLow = Color(0xFF1B1B1F),
    surfaceContainer = Color(0xFF1F1F23),
    surfaceContainerHigh = Color(0xFF2A2A2E),
    surfaceContainerHighest = Color(0xFF353539),
)
