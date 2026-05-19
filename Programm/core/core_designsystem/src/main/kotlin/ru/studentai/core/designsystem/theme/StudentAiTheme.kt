package ru.studentai.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

/**
 * Корневая тема приложения.
 *
 * Использование:
 * ```
 * setContent {
 *     StudentAiTheme {
 *         AppRoot()
 *     }
 * }
 * ```
 *
 * @param darkTheme       если null — определяется автоматически (`isSystemInDarkTheme`)
 * @param dynamicColor    Material You на Android 12+ (берёт цвета обоев пользователя)
 */
@Composable
public fun StudentAiTheme(
    darkTheme: Boolean? = null,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val isDark = darkTheme ?: isSystemInDarkTheme()
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }
    val semantic = if (isDark) DarkSemanticColors else LightSemanticColors

    CompositionLocalProvider(
        LocalSpacing provides StudentAiSpacing(),
        LocalElevations provides StudentAiElevations(),
        LocalAnimation provides StudentAiAnimation(),
        LocalSemanticColors provides semantic,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = StudentAiTypography,
            shapes = StudentAiShapes,
            content = content,
        )
    }
}

/**
 * Удобный фасад для доступа к токенам темы из Composable-кода:
 * `StudentAiTheme.spacing.md`, `StudentAiTheme.semanticColors.success`, ...
 */
public object StudentAiTheme {

    public val spacing: StudentAiSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalSpacing.current

    public val elevations: StudentAiElevations
        @Composable
        @ReadOnlyComposable
        get() = LocalElevations.current

    public val animation: StudentAiAnimation
        @Composable
        @ReadOnlyComposable
        get() = LocalAnimation.current

    public val semanticColors: StudentAiSemanticColors
        @Composable
        @ReadOnlyComposable
        get() = LocalSemanticColors.current
}
