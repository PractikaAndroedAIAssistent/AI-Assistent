package ru.studentai.core.ui.mvi

/**
 * Маркерный интерфейс одноразовых эффектов: Snackbar, навигация, системные toast'ы,
 * `keyboard.hide()` и т. п.
 *
 * Эффекты ходят через [kotlinx.coroutines.channels.Channel] и доставляются
 * **строго один раз**. Не использовать для рендера — для рендера есть [UiState].
 *
 * Примеры:
 * ```
 * sealed interface AuthEffect : UiEffect {
 *     data object NavigateHome : AuthEffect
 *     data class ShowSnackbar(val message: String) : AuthEffect
 * }
 * ```
 */
public interface UiEffect
