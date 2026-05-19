package ru.studentai.core.ui.mvi

import androidx.compose.runtime.Stable

/**
 * Маркерный интерфейс состояния экрана.
 *
 * Все реализации должны быть [androidx.compose.runtime.Immutable] или [Stable]
 * `data class` — это гарантирует, что Compose корректно определяет необходимость
 * перерисовки (skippable functions, structural equality).
 *
 * Запрещено хранить в UiState изменяемые коллекции, mutableStateOf, или ссылки
 * на ViewModel/Repository. UiState — снимок данных для отрисовки.
 */
@Stable
public interface UiState
