package ru.studentai.core.ui.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.studentai.core.common.dispatchers.DispatcherProvider
import ru.studentai.core.common.error.AppException
import ru.studentai.core.common.logger.Logger
import ru.studentai.core.common.logger.NoOpLogger
import ru.studentai.core.common.result.DomainResult
import ru.studentai.core.common.result.safeCall

/**
 * Базовый MVI-ViewModel для всех фич проекта.
 *
 * @param S тип состояния экрана ([UiState] sealed-наследник)
 * @param E тип событий UI ([UiEvent] sealed-наследник)
 * @param F тип одноразовых эффектов ([UiEffect] sealed-наследник)
 *
 * @param initialState   стартовое состояние (Loading/Idle/...)
 * @param dispatchers    провайдер dispatcher'ов (никогда не использовать `Dispatchers.*` напрямую)
 * @param logger         логгер; по умолчанию [NoOpLogger]
 *
 * ## Контракт
 * 1. UI наблюдает [state] (StateFlow) и [effects] (Flow от Channel).
 * 2. UI вызывает [dispatch] с конкретным [UiEvent].
 * 3. Подкласс реализует [handleEvent] — единственная точка входа изменения состояния.
 * 4. Подкласс изменяет состояние **только** через [updateState], отправляет эффекты — через [sendEffect].
 *
 * ## Гарантии
 *  • эффекты доставляются ровно один раз (Channel.BUFFERED);
 *  • при отсутствии collector'а эффекты буферизуются (до 64 элементов);
 *  • любые исключения внутри [launchSafe] логируются и оборачиваются в [DomainResult.Failure].
 */
public abstract class BaseViewModel<S : UiState, E : UiEvent, F : UiEffect>(
    initialState: S,
    protected val dispatchers: DispatcherProvider,
    protected val logger: Logger = NoOpLogger,
) : ViewModel() {

    private val _state: MutableStateFlow<S> = MutableStateFlow(initialState)
    public val state: StateFlow<S> = _state.asStateFlow()

    /** Текущее состояние снапшотом, для синхронного чтения внутри подкласса. */
    protected val currentState: S
        get() = _state.value

    private val _effects: Channel<F> = Channel(capacity = EFFECT_CHANNEL_CAPACITY)
    public val effects: Flow<F> = _effects.receiveAsFlow()

    /**
     * Точка входа любого UI-действия. Сабкласс предоставляет реализацию через [handleEvent].
     * Метод не suspend — он не блокирует UI; асинхронные операции запускаются внутри
     * через [launchSafe].
     */
    public fun dispatch(event: E) {
        logger.debug(TAG, "dispatch: ${event::class.simpleName}")
        handleEvent(event)
    }

    /**
     * Реализуется подклассом. Должен быть exhaustive `when` по sealed-иерархии `E`.
     */
    protected abstract fun handleEvent(event: E)

    /**
     * Иммутабельное обновление состояния.
     * Безопасно вызывать из любого потока (StateFlow.update сам синхронизирует).
     */
    protected fun updateState(transform: (S) -> S) {
        _state.update(transform)
    }

    /**
     * Отправка одноразового эффекта.
     * При переполнении буфера старые эффекты будут потеряны через `trySend` fallback,
     * поэтому здесь используется `send` через корутину — гарантирует доставку.
     */
    protected fun sendEffect(effect: F) {
        viewModelScope.launch { _effects.send(effect) }
    }

    /**
     * Запуск корутины с защитой:
     *  • CancellationException пробрасывается (structured concurrency);
     *  • прочие [AppException] передаются в [onError];
     *  • неожиданные [Throwable] логируются как `error`.
     *
     * @param dispatcher dispatcher выполнения (по умолчанию IO)
     */
    protected fun launchSafe(
        dispatcher: CoroutineDispatcher = dispatchers.io,
        onError: (AppException) -> Unit = ::defaultErrorHandler,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = viewModelScope.launch(dispatcher) {
        when (val result = safeCall { block() }) {
            is DomainResult.Success -> Unit
            is DomainResult.Failure -> {
                logger.error(TAG, "launchSafe failed: ${result.error.message}", result.error)
                onError(result.error)
            }
        }
    }

    /**
     * Хелпер для подписки на `Flow<DomainResult<T>>` с разделением успеха и ошибки.
     * Возвращает [Job] — можно сохранить, отменить, заменить.
     */
    protected fun <T> Flow<DomainResult<T>>.collectResult(
        onSuccess: (T) -> Unit,
        onError: (AppException) -> Unit = ::defaultErrorHandler,
    ): Job = viewModelScope.launch {
        collect { result ->
            when (result) {
                is DomainResult.Success -> onSuccess(result.value)
                is DomainResult.Failure -> {
                    logger.error(TAG, "collectResult failed: ${result.error.message}", result.error)
                    onError(result.error)
                }
            }
        }
    }

    /**
     * Перегружаемый дефолтный обработчик. Подкласс может переопределить чтобы,
     * например, эмитить ShowSnackbar effect одной строчкой:
     * ```
     * override fun defaultErrorHandler(error: AppException) {
     *     sendEffect(MyEffect.ShowError(errorResolver.resolve(error)))
     * }
     * ```
     */
    protected open fun defaultErrorHandler(error: AppException) {
        // Базовый обработчик только логирует. Подкласс должен переопределить,
        // если хочет показывать ошибку пользователю.
    }

    private companion object {
        const val TAG = "BaseViewModel"
        const val EFFECT_CHANNEL_CAPACITY = 64
    }
}
