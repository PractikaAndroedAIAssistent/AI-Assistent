package ru.studentai.core.ui.snackbar

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Глобальный контроллер снэкбаров приложения.
 *
 * Цель: ViewModel'и из feature-модулей могут вызывать [showMessage] и UI верхнего уровня
 * (root Scaffold) подпишется на [messages] и отобразит сообщения, не завися от того,
 * на каком конкретно экране пользователь находится.
 *
 * Реализация: [MutableSharedFlow] с буфером — гарантирует, что если в момент эмита
 * никто не подписан (например, во время навигации), сообщения не теряются.
 */
@Singleton
public class SnackbarController @Inject constructor() {

    private val _messages: MutableSharedFlow<SnackbarMessage> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = BUFFER_CAPACITY,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    public val messages: SharedFlow<SnackbarMessage> = _messages.asSharedFlow()

    /**
     * Отправляет сообщение. Не suspend — использует tryEmit; при переполнении буфера
     * самые старые сообщения отбрасываются.
     */
    public fun showMessage(message: SnackbarMessage) {
        _messages.tryEmit(message)
    }

    public fun showInfo(text: String, actionLabel: String? = null): Unit =
        showMessage(SnackbarMessage(text, actionLabel, SnackbarType.Info))

    public fun showSuccess(text: String, actionLabel: String? = null): Unit =
        showMessage(SnackbarMessage(text, actionLabel, SnackbarType.Success))

    public fun showWarning(text: String, actionLabel: String? = null): Unit =
        showMessage(SnackbarMessage(text, actionLabel, SnackbarType.Warning))

    public fun showError(text: String, actionLabel: String? = null): Unit =
        showMessage(SnackbarMessage(text, actionLabel, SnackbarType.Error))

    private companion object {
        const val BUFFER_CAPACITY = 8
    }
}
