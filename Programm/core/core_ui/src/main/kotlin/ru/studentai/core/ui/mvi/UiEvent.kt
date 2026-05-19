package ru.studentai.core.ui.mvi

/**
 * Маркерный интерфейс пользовательских действий.
 *
 * Все события feature_* реализуют этот интерфейс через `sealed interface`,
 * что даёт exhaustive `when` в [BaseViewModel.handleEvent].
 *
 * Примеры (псевдо):
 * ```
 * sealed interface AuthEvent : UiEvent {
 *     data class EmailChanged(val value: String) : AuthEvent
 *     data class PasswordChanged(val value: String) : AuthEvent
 *     data object LoginClicked : AuthEvent
 * }
 * ```
 */
public interface UiEvent
