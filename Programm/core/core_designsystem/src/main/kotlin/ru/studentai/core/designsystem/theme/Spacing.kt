package ru.studentai.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Spacing scale в стиле Material 3 (4dp baseline).
 *
 * Доступ из Composable: `StudentAiTheme.spacing.md`.
 * Не хардкодьте `8.dp` в коде — используйте токены, чтобы при ребрендинге
 * можно было сместить всю сетку одной правкой.
 */
@Immutable
public data class StudentAiSpacing(
    val none: Dp = 0.dp,
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp,
    val xxxl: Dp = 64.dp,
    /** Стандартный горизонтальный отступ экрана (контент-инсет). */
    val screenHorizontal: Dp = 16.dp,
    /** Стандартный вертикальный отступ экрана. */
    val screenVertical: Dp = 16.dp,
    /** Высота тапабельного элемента (Material accessibility). */
    val minTouchTarget: Dp = 48.dp,
)

public val LocalSpacing: androidx.compose.runtime.ProvidableCompositionLocal<StudentAiSpacing> =
    compositionLocalOf { StudentAiSpacing() }
