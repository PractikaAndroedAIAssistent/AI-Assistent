package ru.studentai.core.ui.snackbar

import androidx.compose.runtime.Immutable

/**
 * Семантический тип снэкбара. Влияет на стиль (цвет фона / иконку) в host'е.
 */
@Immutable
public enum class SnackbarType { Info, Success, Warning, Error }

/**
 * Длительность отображения снэкбара. Намеренно не зависит от `material3.SnackbarDuration`,
 * чтобы `core_ui` оставался без UI-зависимостей.
 *
 * Host (`core_designsystem` или app) мапит [SnackbarLength] на
 * `androidx.compose.material3.SnackbarDuration` при отображении.
 */
@Immutable
public enum class SnackbarLength { Short, Long, Indefinite }

/**
 * Сообщение для отображения в снэкбаре.
 *
 * @param text          текст сообщения (уже локализован)
 * @param actionLabel   необязательный лейбл кнопки действия
 * @param type          семантический тип (цвет/иконка)
 * @param length        длительность отображения
 */
@Immutable
public data class SnackbarMessage(
    val text: String,
    val actionLabel: String? = null,
    val type: SnackbarType = SnackbarType.Info,
    val length: SnackbarLength = SnackbarLength.Short,
)
