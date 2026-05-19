package ru.studentai.core.designsystem.component.button

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp

/**
 * Внутренние дефолты для всех кнопок дизайн-системы.
 * Вынесены отдельно, чтобы любой класс кнопок (Primary/Secondary/Tertiary) имел
 * одинаковую высоту, padding и иконо-текстовое расстояние.
 */
internal object StudentAiButtonDefaults {
    /** Высота тапабельной зоны (Material accessibility). */
    val MinHeight = 48.dp

    val ContentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    val IconPadding = 8.dp
    val IconSize = 18.dp
    val LoadingIndicatorSize = 18.dp
    val LoadingIndicatorStrokeWidth = 2.dp
}
