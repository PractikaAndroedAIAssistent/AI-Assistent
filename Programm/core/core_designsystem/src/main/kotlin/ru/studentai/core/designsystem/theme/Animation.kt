package ru.studentai.core.designsystem.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf

/**
 * Параметры анимаций в стиле Material 3 «Motion» guidelines.
 *
 * Длительности подобраны для мобильных устройств:
 *  • short  150ms — мгновенная обратная связь (ripple, press);
 *  • medium 300ms — переходы между состояниями экрана;
 *  • long   500ms — переходы между экранами / hero-анимации.
 */
@Immutable
public data class StudentAiAnimation(
    val durationShortMs: Int = 150,
    val durationMediumMs: Int = 300,
    val durationLongMs: Int = 500,
    val durationExtraLongMs: Int = 700,
    /** Стандартное easing для большинства переходов. */
    val standard: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f),
    /** Акцентное easing для «впечатляющих» переходов. */
    val emphasized: Easing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f),
    /** Easing для уходящих элементов. */
    val emphasizedAccelerate: Easing = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f),
    /** Easing для появляющихся элементов. */
    val emphasizedDecelerate: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f),
)

public val LocalAnimation: androidx.compose.runtime.ProvidableCompositionLocal<StudentAiAnimation> =
    compositionLocalOf { StudentAiAnimation() }
