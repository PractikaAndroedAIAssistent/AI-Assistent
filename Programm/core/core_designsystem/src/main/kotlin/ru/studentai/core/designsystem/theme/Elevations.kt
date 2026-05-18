package ru.studentai.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Material 3 elevation tokens.
 * Использовать вместо магических `.dp` значений в коде.
 */
@Immutable
public data class StudentAiElevations(
    val level0: Dp = 0.dp,
    val level1: Dp = 1.dp,
    val level2: Dp = 3.dp,
    val level3: Dp = 6.dp,
    val level4: Dp = 8.dp,
    val level5: Dp = 12.dp,
)

public val LocalElevations: androidx.compose.runtime.ProvidableCompositionLocal<StudentAiElevations> =
    compositionLocalOf { StudentAiElevations() }
